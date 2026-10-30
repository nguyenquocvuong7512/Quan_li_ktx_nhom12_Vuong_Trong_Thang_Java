/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * scene3d.js - Core Three.js Scene, Camera, Lighting & Holographic Pedestal
 * Theme: AURORA GLASS – PREMIUM HOLOGRAPHIC CAMPUS 3D
 */

const Scene3D = (function () {
    let container = null;
    let scene = null;
    let camera = null;
    let renderer = null;
    let controls = null;
    let clock = new THREE.Clock();
    let animFrameId = null;
    let isRunning = false;

    // Hologram elements
    let radarRing = null;
    let particles = null;
    let pedestalGroup = null;
    let lightsGroup = null;

    // View state - Optimized cinematic framing for 60% viewport
    const DEFAULT_CAMERA_POS = { x: 32, y: 26, z: 40 };
    const DEFAULT_TARGET_POS = { x: 0, y: 2.2, z: 0 };

    function init(containerElement) {
        if (!containerElement) {
            console.error('[Scene3D] containerElement không tồn tại!');
            return null;
        }
        container = containerElement;

        const width = container.clientWidth || window.innerWidth;
        const height = container.clientHeight || 560;

        // 1. Scene
        scene = new THREE.Scene();
        // Bright White & Cyan Blue theme background
        scene.background = new THREE.Color(0xF8FAFF);
        scene.fog = new THREE.FogExp2(0xF8FAFF, 0.011);

        // 2. Camera
        camera = new THREE.PerspectiveCamera(40, width / height, 0.1, 1000);
        camera.position.set(DEFAULT_CAMERA_POS.x, DEFAULT_CAMERA_POS.y, DEFAULT_CAMERA_POS.z);

        // 3. Renderer
        renderer = new THREE.WebGLRenderer({
            antialias: true,
            alpha: false,
            powerPreference: 'high-performance'
        });
        renderer.setSize(width, height);
        renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        renderer.shadowMap.enabled = true;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        renderer.toneMapping = THREE.ACESFilmicToneMapping;
        renderer.toneMappingExposure = 1.18;
        container.appendChild(renderer.domElement);

        // 4. OrbitControls
        controls = new THREE.OrbitControls(camera, renderer.domElement);
        controls.enableDamping = true;
        controls.dampingFactor = 0.05;
        controls.target.set(DEFAULT_TARGET_POS.x, DEFAULT_TARGET_POS.y, DEFAULT_TARGET_POS.z);
        controls.minDistance = 14;
        controls.maxDistance = 105;
        controls.maxPolarAngle = Math.PI / 2 - 0.04; // Don't go below horizon
        controls.enablePan = true;

        // 5. Lighting
        setupLighting();

        // 6. Holographic Pedestal & Grounds
        setupHologramEnvironment();

        // 7. Event Listeners
        window.addEventListener('resize', onWindowResize, false);
        document.addEventListener('visibilitychange', onVisibilityChange, false);

        // 8. Start Loop
        start();

        console.log('[Scene3D] Khởi tạo WebGL Scene3D thành công!');
        return { scene, camera, renderer, controls };
    }

    function setupLighting() {
        lightsGroup = new THREE.Group();

        // Ambient Light - Soft sky blue tint
        const ambientLight = new THREE.AmbientLight(0xE0F2FE, 1.05);
        lightsGroup.add(ambientLight);

        // Main Sun / Key Light - Architectural sunlight
        const mainSun = new THREE.DirectionalLight(0xFFFFFF, 1.35);
        mainSun.position.set(38, 55, 32);
        mainSun.castShadow = true;
        mainSun.shadow.mapSize.width = 2048;
        mainSun.shadow.mapSize.height = 2048;
        mainSun.shadow.camera.near = 10;
        mainSun.shadow.camera.far = 150;
        mainSun.shadow.camera.left = -45;
        mainSun.shadow.camera.right = 45;
        mainSun.shadow.camera.top = 45;
        mainSun.shadow.camera.bottom = -45;
        mainSun.shadow.bias = -0.0004;
        lightsGroup.add(mainSun);

        // Fill Light - Ice Cyan from opposite angle
        const fillLight = new THREE.DirectionalLight(0xBAE6FD, 0.5);
        fillLight.position.set(-30, 25, -25);
        lightsGroup.add(fillLight);

        // Center Hologram Glow Light (Cyan #00C8F8)
        const centerGlow = new THREE.PointLight(0x00C8F8, 2.6, 42);
        centerGlow.position.set(0, 1.2, 0);
        lightsGroup.add(centerGlow);

        scene.add(lightsGroup);
    }

    function setupHologramEnvironment() {
        pedestalGroup = new THREE.Group();

        // Layer 1: Outer Pedestal Ring (Crisp white platform with bevel)
        const baseGeo = new THREE.CylinderGeometry(34, 35, 1.2, 64);
        const baseMat = new THREE.MeshPhysicalMaterial({
            color: 0xFFFFFF,
            metalness: 0.08,
            roughness: 0.2,
            clearcoat: 0.45,
            clearcoatRoughness: 0.1
        });
        const baseMesh = new THREE.Mesh(baseGeo, baseMat);
        baseMesh.position.y = -0.6;
        baseMesh.receiveShadow = true;
        pedestalGroup.add(baseMesh);

        // Layer 2: Glowing Cyan Border Ring
        const ringGeo = new THREE.TorusGeometry(34.2, 0.18, 16, 64);
        const ringMat = new THREE.MeshBasicMaterial({
            color: 0x00C8F8,
            transparent: true,
            opacity: 0.85
        });
        const glowRing = new THREE.Mesh(ringGeo, ringMat);
        glowRing.rotation.x = Math.PI / 2;
        glowRing.position.y = 0.05;
        pedestalGroup.add(glowRing);

        // Layer 3: Inner Pedestal Disc (Ice Blue)
        const innerGeo = new THREE.CylinderGeometry(32.5, 32.5, 0.1, 64);
        const innerMat = new THREE.MeshStandardMaterial({
            color: 0xF1F8FD,
            roughness: 0.35,
            metalness: 0.15
        });
        const innerDisc = new THREE.Mesh(innerGeo, innerMat);
        innerDisc.position.y = 0.02;
        innerDisc.receiveShadow = true;
        pedestalGroup.add(innerDisc);

        // Layer 4: Sci-Fi Hologram Grid
        const gridHelper = new THREE.GridHelper(62, 38, 0x00C8F8, 0xCBD5E1);
        gridHelper.position.y = 0.08;
        gridHelper.material.transparent = true;
        gridHelper.material.opacity = 0.38;
        pedestalGroup.add(gridHelper);

        // Layer 5: Rotating Radar Scan Ring
        const radarCanvas = document.createElement('canvas');
        radarCanvas.width = 512;
        radarCanvas.height = 512;
        const ctx = radarCanvas.getContext('2d');

        // Draw radar gradient sweep
        const grad = ctx.createConicGradient(0, 256, 256);
        grad.addColorStop(0, 'rgba(0, 200, 248, 0.55)');
        grad.addColorStop(0.25, 'rgba(0, 200, 248, 0.08)');
        grad.addColorStop(1, 'rgba(0, 200, 248, 0.0)');

        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.arc(256, 256, 240, 0, Math.PI * 2);
        ctx.fill();

        // Concentric circular marks
        ctx.strokeStyle = 'rgba(0, 200, 248, 0.65)';
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.arc(256, 256, 240, 0, Math.PI * 2);
        ctx.stroke();

        ctx.strokeStyle = 'rgba(0, 200, 248, 0.35)';
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.arc(256, 256, 160, 0, Math.PI * 2);
        ctx.arc(256, 256, 80, 0, Math.PI * 2);
        ctx.stroke();

        const radarTex = new THREE.CanvasTexture(radarCanvas);
        const radarGeo = new THREE.PlaneGeometry(58, 58);
        const radarMat = new THREE.MeshBasicMaterial({
            map: radarTex,
            transparent: true,
            opacity: 0.6,
            depthWrite: false,
            side: THREE.DoubleSide
        });
        radarRing = new THREE.Mesh(radarGeo, radarMat);
        radarRing.rotation.x = -Math.PI / 2;
        radarRing.position.y = 0.12;
        pedestalGroup.add(radarRing);

        // Layer 6: Floating Cyan Data Particles / Nodes
        const particleCount = 110;
        const pGeo = new THREE.BufferGeometry();
        const positions = new Float32Array(particleCount * 3);
        const scales = new Float32Array(particleCount);

        for (let i = 0; i < particleCount; i++) {
            const rad = 5 + Math.random() * 25;
            const theta = Math.random() * Math.PI * 2;
            positions[i * 3] = Math.cos(theta) * rad;
            positions[i * 3 + 1] = 0.5 + Math.random() * 14;
            positions[i * 3 + 2] = Math.sin(theta) * rad;
            scales[i] = Math.random();
        }
        pGeo.setAttribute('position', new THREE.BufferAttribute(positions, 3));

        const pMat = new THREE.PointsMaterial({
            color: 0x00C8F8,
            size: 0.32,
            transparent: true,
            opacity: 0.6,
            blending: THREE.AdditiveBlending
        });
        particles = new THREE.Points(pGeo, pMat);
        pedestalGroup.add(particles);

        scene.add(pedestalGroup);
    }

    function onWindowResize() {
        if (!container || !renderer || !camera) return;
        const width = container.clientWidth || window.innerWidth;
        const height = container.clientHeight || 560;
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        renderer.setSize(width, height);
    }

    function onVisibilityChange() {
        if (document.hidden) {
            stop();
        } else {
            start();
        }
    }

    function animate() {
        if (!isRunning) return;
        animFrameId = requestAnimationFrame(animate);

        const delta = clock.getDelta();
        const elapsedTime = clock.getElapsedTime();

        // Rotate Hologram Radar Sweep
        if (radarRing) {
            radarRing.rotation.z -= delta * 0.45;
        }

        // Float Hologram Particles
        if (particles) {
            const posAttr = particles.geometry.attributes.position;
            for (let i = 0; i < posAttr.count; i++) {
                let y = posAttr.getY(i);
                y += delta * 0.6;
                if (y > 15) y = 0.5;
                posAttr.setY(i, y);
            }
            posAttr.needsUpdate = true;
            particles.rotation.y += delta * 0.05;
        }

        // Update camera controls
        if (controls) {
            controls.update();
        }

        // Trigger hooks
        if (window.Campus3D && typeof window.Campus3D.onUpdate === 'function') {
            window.Campus3D.onUpdate(elapsedTime, delta);
        }

        renderer.render(scene, camera);
    }

    function start() {
        if (!isRunning) {
            isRunning = true;
            clock.start();
            animate();
        }
    }

    function stop() {
        if (isRunning) {
            isRunning = false;
            if (animFrameId) cancelAnimationFrame(animFrameId);
        }
    }

    function resetCamera(duration = 1.4, onComplete = null) {
        if (!window.gsap || !camera || !controls) {
            camera.position.set(DEFAULT_CAMERA_POS.x, DEFAULT_CAMERA_POS.y, DEFAULT_CAMERA_POS.z);
            controls.target.set(DEFAULT_TARGET_POS.x, DEFAULT_TARGET_POS.y, DEFAULT_TARGET_POS.z);
            controls.update();
            if (onComplete) onComplete();
            return;
        }

        gsap.to(camera.position, {
            x: DEFAULT_CAMERA_POS.x,
            y: DEFAULT_CAMERA_POS.y,
            z: DEFAULT_CAMERA_POS.z,
            duration: duration,
            ease: 'power2.inOut'
        });

        gsap.to(controls.target, {
            x: DEFAULT_TARGET_POS.x,
            y: DEFAULT_TARGET_POS.y,
            z: DEFAULT_TARGET_POS.z,
            duration: duration,
            ease: 'power2.inOut',
            onUpdate: () => controls.update(),
            onComplete: () => {
                if (onComplete) onComplete();
            }
        });
    }

    return {
        init,
        start,
        stop,
        resetCamera,
        getScene: () => scene,
        getCamera: () => camera,
        getRenderer: () => renderer,
        getControls: () => controls,
        getDefaultCameraPos: () => DEFAULT_CAMERA_POS,
        getDefaultTargetPos: () => DEFAULT_TARGET_POS
    };
})();

window.Scene3D = Scene3D;
