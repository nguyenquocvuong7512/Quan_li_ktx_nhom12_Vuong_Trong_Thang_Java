/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * campus3d.js - Procedural 3D Campus Builder from Database
 * Maps Khu -> ToaNha -> Tang -> Phong dynamically
 * Upgraded to AURORA GLASS – PREMIUM HOLOGRAPHIC CAMPUS 3D style
 */

const Campus3D = (function () {
    let campusGroup = null;
    let buildingMeshes = [];
    let treeMeshes = [];
    let pathwayMeshes = [];
    let dataTrails = [];
    let pulseTime = 0;

    // Building positioning coordinates on the holographic campus
    const DEFAULT_LAYOUT_SLOTS = [
        { x: -14, z: -10, rotY: 0.15, tagType: 'camera' },     // A1: An ninh & Giám sát
        { x: 14, z: -10, rotY: -0.15, tagType: 'power' },      // A2: Điện năng & Tiện ích
        { x: 0, z: -18, rotY: 0, tagType: 'facility' },         // A3: Cơ sở vật chất trung tâm
        { x: -16, z: 12, rotY: 0.25, tagType: 'online' },       // B1: Quốc tế (Online 98%)
        { x: 16, z: 12, rotY: -0.25, tagType: 'parking' },      // B2: Nghiên cứu
        { x: 0, z: 22, rotY: Math.PI, tagType: 'env' }          // C1: Phân khu sinh thái
    ];

    // Shared Architectural Materials (Bright White & Cyan Blue)
    const MATERIALS = {
        facadeWhite: new THREE.MeshPhysicalMaterial({
            color: 0xFAFCFF,
            roughness: 0.22,
            metalness: 0.08,
            clearcoat: 0.45,
            clearcoatRoughness: 0.12
        }),
        facadeAccentBlue: new THREE.MeshPhysicalMaterial({
            color: 0x2563EB,
            roughness: 0.2,
            metalness: 0.35,
            clearcoat: 0.25
        }),
        windowGlass: new THREE.MeshPhysicalMaterial({
            color: 0x00C8F8,
            emissive: 0x003355,
            emissiveIntensity: 0.4,
            roughness: 0.1,
            metalness: 0.85,
            transparent: true,
            opacity: 0.88
        }),
        windowGlassGlowing: new THREE.MeshStandardMaterial({
            color: 0x38BDF8,
            emissive: 0x00C8F8,
            emissiveIntensity: 0.8,
            roughness: 0.08
        }),
        cyanLedStrip: new THREE.MeshBasicMaterial({
            color: 0x00C8F8
        }),
        roofEquipment: new THREE.MeshStandardMaterial({
            color: 0x94A3B8,
            roughness: 0.4,
            metalness: 0.5
        }),
        solarPanel: new THREE.MeshStandardMaterial({
            color: 0x1E3A8A,
            roughness: 0.12,
            metalness: 0.9
        }),
        pavement: new THREE.MeshStandardMaterial({
            color: 0xF1F5F9,
            roughness: 0.65,
            metalness: 0.05
        }),
        roadDark: new THREE.MeshStandardMaterial({
            color: 0x334155,
            roughness: 0.8
        }),
        waterCyan: new THREE.MeshPhysicalMaterial({
            color: 0x00C8F8,
            roughness: 0.05,
            metalness: 0.2,
            transmission: 0.6,
            transparent: true,
            opacity: 0.75
        }),
        treeFoliage: new THREE.MeshStandardMaterial({
            color: 0x10B981,
            roughness: 0.5
        }),
        treeTrunk: new THREE.MeshStandardMaterial({
            color: 0x64748B,
            roughness: 0.7
        })
    };

    function buildCampus(buildingsData) {
        const scene = Scene3D.getScene();
        if (!scene) return;

        // Clean previous campus
        if (campusGroup) {
            scene.remove(campusGroup);
            disposeHierarchy(campusGroup);
        }

        campusGroup = new THREE.Group();
        buildingMeshes = [];
        treeMeshes = [];
        pathwayMeshes = [];
        dataTrails = [];

        // 1. Build Central Plaza, Water Fountain & Entrance Gate
        buildCentralPlaza();
        buildEntranceGate();

        // 2. Build Each Building from Database Data
        buildingsData.forEach((buildingData, index) => {
            const slot = DEFAULT_LAYOUT_SLOTS[index % DEFAULT_LAYOUT_SLOTS.length];
            const buildingObj = createProceduralBuilding(buildingData, slot, index);
            campusGroup.add(buildingObj);
            buildingMeshes.push(buildingObj);

            // Connect building to central hub with glowing cyber data trail
            createDataTrail(slot.x, slot.z);
        });

        // 3. Add Landscape Trees & Greenery
        buildLandscaping();

        scene.add(campusGroup);
        return campusGroup;
    }

    function createProceduralBuilding(bData, slot, index) {
        const buildingGroup = new THREE.Group();
        buildingGroup.position.set(slot.x, 0, slot.z);
        buildingGroup.rotation.y = slot.rotY || 0;

        // Attach database metadata
        buildingGroup.userData = {
            type: 'building',
            id: bData.id,
            maToa: bData.maToa,
            tenToa: bData.tenToa,
            soTang: bData.soTang || 3,
            data: bData,
            isExploded: false,
            initialY: 0
        };

        const floorCount = bData.soTang || 3;
        const floorHeight = 2.4;
        const buildingWidth = 9.2;
        const buildingDepth = 7.0;

        // Concrete Base Foundation with Blue LED strip
        const baseGeo = new THREE.BoxGeometry(buildingWidth + 1.2, 0.4, buildingDepth + 1.2);
        const baseMesh = new THREE.Mesh(baseGeo, MATERIALS.facadeWhite);
        baseMesh.position.y = 0.2;
        baseMesh.castShadow = true;
        baseMesh.receiveShadow = true;
        buildingGroup.add(baseMesh);

        const baseLedGeo = new THREE.BoxGeometry(buildingWidth + 1.25, 0.08, buildingDepth + 1.25);
        const baseLed = new THREE.Mesh(baseLedGeo, MATERIALS.cyanLedStrip);
        baseLed.position.y = 0.38;
        buildingGroup.add(baseLed);

        // Floors Array for dynamic explosion & inspection
        const floorGroups = [];

        for (let f = 1; f <= floorCount; f++) {
            const floorGroup = new THREE.Group();
            const floorY = 0.4 + (f - 1) * floorHeight;
            floorGroup.position.y = floorY;

            floorGroup.userData = {
                type: 'floor',
                floorIndex: f,
                buildingId: bData.id,
                defaultY: floorY,
                floorData: (bData.floors && bData.floors[f - 1]) || null
            };

            // Main Floor Body (White Architectural Facade)
            const floorBodyGeo = new THREE.BoxGeometry(buildingWidth, floorHeight * 0.94, buildingDepth);
            const floorMesh = new THREE.Mesh(floorBodyGeo, MATERIALS.facadeWhite.clone());
            floorMesh.position.y = floorHeight * 0.47;
            floorMesh.castShadow = true;
            floorMesh.receiveShadow = true;
            floorGroup.add(floorMesh);

            // Windows on Front and Back Facades
            const windowRows = 2;
            const windowCols = 4;
            const winWidth = 1.35;
            const winHeight = 0.75;

            // Front Windows (Z = buildingDepth / 2 + 0.02)
            for (let r = 0; r < windowRows; r++) {
                for (let c = 0; c < windowCols; c++) {
                    const wx = -buildingWidth / 2 + 1.3 + c * 2.2;
                    const wy = 0.5 + r * 1.0;
                    const isLit = (f + c + r) % 3 === 0;
                    const winMat = isLit ? MATERIALS.windowGlassGlowing : MATERIALS.windowGlass;

                    const winGeo = new THREE.PlaneGeometry(winWidth, winHeight);
                    const winMesh = new THREE.Mesh(winGeo, winMat);
                    winMesh.position.set(wx, wy, buildingDepth / 2 + 0.02);
                    floorGroup.add(winMesh);

                    // Window Frame border
                    const frameGeo = new THREE.EdgesGeometry(winGeo);
                    const frameLine = new THREE.LineSegments(frameGeo, new THREE.LineBasicMaterial({ color: 0xCBD5E1 }));
                    frameLine.position.copy(winMesh.position);
                    floorGroup.add(frameLine);
                }
            }

            // Back Windows (Z = -buildingDepth / 2 - 0.02)
            for (let c = 0; c < windowCols; c++) {
                const wx = -buildingWidth / 2 + 1.3 + c * 2.2;
                const wy = 0.9;
                const winGeo = new THREE.PlaneGeometry(winWidth, winHeight);
                const winMesh = new THREE.Mesh(winGeo, MATERIALS.windowGlass);
                winMesh.position.set(wx, wy, -buildingDepth / 2 - 0.02);
                winMesh.rotation.y = Math.PI;
                floorGroup.add(winMesh);
            }

            // Balconies on front for middle floors
            if (f > 1 && f < floorCount) {
                const balconyGeo = new THREE.BoxGeometry(2.4, 0.4, 0.8);
                const balcony = new THREE.Mesh(balconyGeo, MATERIALS.facadeAccentBlue);
                balcony.position.set(0, 0.35, buildingDepth / 2 + 0.4);
                floorGroup.add(balcony);
            }

            // Floor Division Plate / Slab
            const slabGeo = new THREE.BoxGeometry(buildingWidth + 0.4, 0.15, buildingDepth + 0.4);
            const slab = new THREE.Mesh(slabGeo, MATERIALS.facadeWhite);
            slab.position.y = floorHeight;
            floorGroup.add(slab);

            // Cyan LED Floor Indicator Strip
            const ledGeo = new THREE.BoxGeometry(buildingWidth + 0.42, 0.04, buildingDepth + 0.42);
            const led = new THREE.Mesh(ledGeo, MATERIALS.cyanLedStrip);
            led.position.y = floorHeight + 0.08;
            floorGroup.add(led);

            buildingGroup.add(floorGroup);
            floorGroups.push(floorGroup);
        }

        buildingGroup.userData.floorGroups = floorGroups;

        // Ground Floor Entrance Awning
        const awningGeo = new THREE.BoxGeometry(3.6, 0.15, 2.0);
        const awning = new THREE.Mesh(awningGeo, MATERIALS.facadeAccentBlue);
        awning.position.set(0, 1.8, buildingDepth / 2 + 1.0);
        buildingGroup.add(awning);

        // Entrance Door Glass
        const doorGeo = new THREE.PlaneGeometry(1.8, 1.6);
        const door = new THREE.Mesh(doorGeo, MATERIALS.windowGlassGlowing);
        door.position.set(0, 0.9, buildingDepth / 2 + 0.03);
        buildingGroup.add(door);

        // Rooftop Parapet, HVAC & Hologram Antenna Mast
        const totalHeight = 0.4 + floorCount * floorHeight;
        const roofGroup = new THREE.Group();
        roofGroup.position.y = totalHeight;

        // Parapet Wall
        const parapetGeo = new THREE.BoxGeometry(buildingWidth + 0.3, 0.6, buildingDepth + 0.3);
        const parapet = new THREE.Mesh(parapetGeo, MATERIALS.facadeWhite);
        parapet.position.y = 0.3;
        roofGroup.add(parapet);

        // Roof Deck
        const roofDeckGeo = new THREE.BoxGeometry(buildingWidth, 0.1, buildingDepth);
        const roofDeck = new THREE.Mesh(roofDeckGeo, MATERIALS.roofEquipment);
        roofDeck.position.y = 0.05;
        roofGroup.add(roofDeck);

        // HVAC Units
        const hvacGeo = new THREE.BoxGeometry(1.6, 1.1, 1.4);
        const hvac = new THREE.Mesh(hvacGeo, MATERIALS.roofEquipment);
        hvac.position.set(-2.2, 0.65, -1.2);
        roofGroup.add(hvac);

        // Solar Panels
        for (let s = 0; s < 3; s++) {
            const solarGeo = new THREE.BoxGeometry(1.8, 0.08, 1.2);
            const solar = new THREE.Mesh(solarGeo, MATERIALS.solarPanel);
            solar.position.set(1.5, 0.25, -1.8 + s * 1.6);
            solar.rotation.x = 0.25;
            roofGroup.add(solar);
        }

        // Antenna Mast with Blinking Cyan Beacon
        const mastGeo = new THREE.CylinderGeometry(0.06, 0.12, 3.2, 8);
        const mast = new THREE.Mesh(mastGeo, MATERIALS.roofEquipment);
        mast.position.set(0, 1.6, 0);
        roofGroup.add(mast);

        const beaconGeo = new THREE.SphereGeometry(0.2, 16, 16);
        const beaconMat = new THREE.MeshBasicMaterial({ color: 0x00C8F8 });
        const beacon = new THREE.Mesh(beaconGeo, beaconMat);
        beacon.position.set(0, 3.2, 0);
        roofGroup.add(beacon);

        buildingGroup.add(roofGroup);
        buildingGroup.userData.roofGroup = roofGroup;

        // Floating 3D Holographic Label above building (Styled with Smart Campus reference look)
        const labelSprite = createHolographicSmartTag(bData.maToa || `TOA-${index + 1}`, `${floorCount} TẦNG`, slot.tagType);
        labelSprite.position.set(0, totalHeight + 4.2, 0);
        buildingGroup.add(labelSprite);
        buildingGroup.userData.labelSprite = labelSprite;

        return buildingGroup;
    }

    // Creates the High-Tech Smart Badge seen in the reference image
    function createHolographicSmartTag(title, subtitle, tagType = 'facility') {
        const canvas = document.createElement('canvas');
        canvas.width = 300;
        canvas.height = 140;
        const ctx = canvas.getContext('2d');

        // Glass background with Cyan / Sky border
        ctx.fillStyle = 'rgba(255, 255, 255, 0.94)';
        roundRect(ctx, 12, 12, 276, 116, 18);
        ctx.fill();

        ctx.strokeStyle = '#00C8F8';
        ctx.lineWidth = 3.5;
        roundRect(ctx, 12, 12, 276, 116, 18);
        ctx.stroke();

        // Icon Header Badge
        ctx.fillStyle = 'rgba(0, 200, 248, 0.12)';
        roundRect(ctx, 24, 22, 110, 28, 8);
        ctx.fill();

        ctx.fillStyle = '#0284C7';
        ctx.font = 'bold 16px "JetBrains Mono", sans-serif';
        ctx.textAlign = 'center';
        let badgeText = 'SMART';
        if (tagType === 'camera') badgeText = 'SECURITY';
        if (tagType === 'power') badgeText = 'ENERGY';
        if (tagType === 'online') badgeText = '98% LIVE';
        if (tagType === 'parking') badgeText = 'PARKING';
        ctx.fillText(badgeText, 79, 42);

        // Floor count badge on right
        ctx.fillStyle = '#64748B';
        ctx.font = '600 16px "Be Vietnam Pro", sans-serif';
        ctx.textAlign = 'right';
        ctx.fillText(subtitle, 264, 42);

        // Building Name Main Title
        ctx.fillStyle = '#172B4D';
        ctx.font = 'bold 30px "Be Vietnam Pro", sans-serif';
        ctx.textAlign = 'left';
        ctx.fillText(title, 26, 90);

        // Online Pulse Dot
        ctx.fillStyle = '#10B981';
        ctx.beginPath();
        ctx.arc(252, 80, 6, 0, Math.PI * 2);
        ctx.fill();

        // Sub status line
        ctx.fillStyle = '#0EA5E9';
        ctx.font = '600 15px "Be Vietnam Pro", sans-serif';
        ctx.fillText('Digital Twin Active', 26, 114);

        const texture = new THREE.CanvasTexture(canvas);
        const spriteMat = new THREE.SpriteMaterial({
            map: texture,
            transparent: true,
            depthTest: false
        });
        const sprite = new THREE.Sprite(spriteMat);
        sprite.scale.set(7.5, 3.5, 1);
        return sprite;
    }

    function roundRect(ctx, x, y, width, height, radius) {
        ctx.beginPath();
        ctx.moveTo(x + radius, y);
        ctx.lineTo(x + width - radius, y);
        ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
        ctx.lineTo(x + width, y + height - radius);
        ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        ctx.lineTo(x + radius, y + height);
        ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
        ctx.lineTo(x, y + radius);
        ctx.quadraticCurveTo(x, y, x + radius, y);
        ctx.closePath();
    }

    function buildCentralPlaza() {
        // Central Square Platform
        const plazaGeo = new THREE.CylinderGeometry(8.5, 8.5, 0.15, 32);
        const plaza = new THREE.Mesh(plazaGeo, MATERIALS.pavement);
        plaza.position.set(0, 0.1, 0);
        plaza.receiveShadow = true;
        campusGroup.add(plaza);

        // Central Fountain Pool
        const poolGeo = new THREE.CylinderGeometry(3.5, 3.5, 0.25, 32);
        const pool = new THREE.Mesh(poolGeo, MATERIALS.waterCyan);
        pool.position.set(0, 0.2, 0);
        campusGroup.add(pool);

        // Holographic Central Sphere / Globe
        const globeGeo = new THREE.SphereGeometry(1.4, 32, 32);
        const globeMat = new THREE.MeshPhysicalMaterial({
            color: 0x00C8F8,
            emissive: 0x0284C7,
            emissiveIntensity: 0.65,
            roughness: 0.1,
            metalness: 0.9,
            transparent: true,
            opacity: 0.8
        });
        const globe = new THREE.Mesh(globeGeo, globeMat);
        globe.position.set(0, 2.0, 0);
        campusGroup.add(globe);

        // Orbiting rings around central hologram
        const ringGeo = new THREE.TorusGeometry(2.2, 0.04, 16, 64);
        const ringMat = new THREE.MeshBasicMaterial({ color: 0x00C8F8 });
        const ring1 = new THREE.Mesh(ringGeo, ringMat);
        ring1.rotation.x = Math.PI / 3;
        ring1.position.set(0, 2.0, 0);
        campusGroup.add(ring1);
        campusGroup.userData.centerRing1 = ring1;

        const ring2 = new THREE.Mesh(ringGeo, ringMat);
        ring2.rotation.y = Math.PI / 4;
        ring2.position.set(0, 2.0, 0);
        campusGroup.add(ring2);
        campusGroup.userData.centerRing2 = ring2;
    }

    function buildEntranceGate() {
        const gateGroup = new THREE.Group();
        gateGroup.position.set(0, 0, 24);

        // Entrance Gate Arch Pillars
        const pillarGeo = new THREE.BoxGeometry(0.8, 3.5, 0.8);
        const leftPillar = new THREE.Mesh(pillarGeo, MATERIALS.facadeAccentBlue);
        leftPillar.position.set(-4.5, 1.75, 0);
        gateGroup.add(leftPillar);

        const rightPillar = new THREE.Mesh(pillarGeo, MATERIALS.facadeAccentBlue);
        rightPillar.position.set(4.5, 1.75, 0);
        gateGroup.add(rightPillar);

        // Cross Beam
        const beamGeo = new THREE.BoxGeometry(10.2, 0.6, 1.0);
        const beam = new THREE.Mesh(beamGeo, MATERIALS.facadeWhite);
        beam.position.set(0, 3.6, 0);
        gateGroup.add(beam);

        // LED Strip on Gate
        const ledGeo = new THREE.BoxGeometry(10.3, 0.08, 1.05);
        const led = new THREE.Mesh(ledGeo, MATERIALS.cyanLedStrip);
        led.position.set(0, 3.92, 0);
        gateGroup.add(led);

        campusGroup.add(gateGroup);
    }

    function createDataTrail(targetX, targetZ) {
        const curve = new THREE.QuadraticBezierCurve3(
            new THREE.Vector3(0, 0.15, 0),
            new THREE.Vector3(targetX * 0.5, 0.35, targetZ * 0.5),
            new THREE.Vector3(targetX * 0.82, 0.15, targetZ * 0.82)
        );

        const points = curve.getPoints(30);
        const geo = new THREE.BufferGeometry().setFromPoints(points);
        const mat = new THREE.LineBasicMaterial({
            color: 0x00C8F8,
            transparent: true,
            opacity: 0.65,
            linewidth: 2
        });
        const line = new THREE.Line(geo, mat);
        campusGroup.add(line);
        dataTrails.push(line);
    }

    function buildLandscaping() {
        const treePositions = [
            [-7, -4], [7, -4], [-8, 4], [8, 4],
            [-22, -3], [22, -3], [-5, 14], [5, 14],
            [-12, -20], [12, -20], [0, -9]
        ];

        treePositions.forEach(([tx, tz]) => {
            const tree = createModernTree();
            tree.position.set(tx, 0.1, tz);
            campusGroup.add(tree);
            treeMeshes.push(tree);
        });
    }

    function createModernTree() {
        const treeGroup = new THREE.Group();

        // White architectural trunk
        const trunkGeo = new THREE.CylinderGeometry(0.12, 0.18, 1.2, 8);
        const trunk = new THREE.Mesh(trunkGeo, MATERIALS.treeTrunk);
        trunk.position.y = 0.6;
        trunk.castShadow = true;
        treeGroup.add(trunk);

        // Stylized geometric foliage
        const foliageGeo = new THREE.ConeGeometry(0.85, 1.8, 7);
        const foliage = new THREE.Mesh(foliageGeo, MATERIALS.treeFoliage);
        foliage.position.y = 1.8;
        foliage.castShadow = true;
        treeGroup.add(foliage);

        return treeGroup;
    }

    function onUpdate(elapsedTime, delta) {
        // Spin central hologram rings
        if (campusGroup && campusGroup.userData) {
            if (campusGroup.userData.centerRing1) {
                campusGroup.userData.centerRing1.rotation.z += delta * 0.55;
            }
            if (campusGroup.userData.centerRing2) {
                campusGroup.userData.centerRing2.rotation.x += delta * 0.45;
            }
        }

        // Gentle pulse on glowing materials
        pulseTime += delta * 2;
        const glow = 0.5 + Math.sin(pulseTime) * 0.25;
        MATERIALS.windowGlassGlowing.emissiveIntensity = glow;
    }

    function disposeHierarchy(obj) {
        obj.traverse((child) => {
            if (child.geometry) child.geometry.dispose();
            if (child.material) {
                if (Array.isArray(child.material)) {
                    child.material.forEach(m => m.dispose());
                } else {
                    child.material.dispose();
                }
            }
        });
    }

    return {
        buildCampus,
        getBuildings: () => buildingMeshes,
        getBuildingById: (id) => buildingMeshes.find(b => b.userData.id == id),
        onUpdate
    };
})();

window.Campus3D = Campus3D;
