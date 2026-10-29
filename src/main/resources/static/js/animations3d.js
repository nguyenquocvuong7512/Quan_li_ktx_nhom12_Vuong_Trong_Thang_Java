/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * animations3d.js - GSAP Camera Transitions & Architectural Animations
 */

const Animations3D = (function () {
    let activeTween = null;
    let isExploded = false;

    // Fly camera to a specific building with cinematic framing
    function flyToBuilding(buildingGroup, onComplete) {
        if (!buildingGroup) return;

        const camera = Scene3D.getCamera();
        const controls = Scene3D.getControls();
        if (!camera || !controls || !window.gsap) return;

        const bPos = buildingGroup.position;
        const bHeight = (buildingGroup.userData.soTang || 3) * 2.4;

        // Target center of building
        const targetX = bPos.x;
        const targetY = bHeight * 0.55;
        const targetZ = bPos.z;

        // Position camera in front and slightly above
        const distance = 16 + bHeight * 0.45;
        const camX = bPos.x + distance * 0.7;
        const camY = targetY + distance * 0.55;
        const camZ = bPos.z + distance * 0.7;

        // Kill any previous camera tweens
        gsap.killTweensOf(camera.position);
        gsap.killTweensOf(controls.target);

        const tl = gsap.timeline({
            onUpdate: () => controls.update(),
            onComplete: () => {
                if (typeof onComplete === 'function') onComplete();
            }
        });

        tl.to(controls.target, {
            x: targetX,
            y: targetY,
            z: targetZ,
            duration: 1.4,
            ease: 'power2.inOut'
        }, 0);

        tl.to(camera.position, {
            x: camX,
            y: camY,
            z: camZ,
            duration: 1.6,
            ease: 'power2.inOut'
        }, 0);

        return tl;
    }

    // Reset camera to wide overview
    function resetToOverview(onComplete) {
        return Scene3D.resetCamera(1.5, onComplete);
    }

    // Explode Floors (Tách tầng)
    function toggleExplodeFloors(buildingGroup, shouldExplode) {
        if (!buildingGroup || !buildingGroup.userData || !buildingGroup.userData.floorGroups) return;
        if (!window.gsap) return;

        const floorGroups = buildingGroup.userData.floorGroups;
        const roofGroup = buildingGroup.userData.roofGroup;
        const labelSprite = buildingGroup.userData.labelSprite;

        isExploded = (typeof shouldExplode === 'boolean') ? shouldExplode : !buildingGroup.userData.isExploded;
        buildingGroup.userData.isExploded = isExploded;

        const separationSpacing = 2.4;

        floorGroups.forEach((fl, idx) => {
            const defaultY = fl.userData.defaultY;
            const targetY = isExploded ? (defaultY + idx * separationSpacing) : defaultY;

            gsap.to(fl.position, {
                y: targetY,
                duration: 0.9,
                delay: idx * 0.06,
                ease: isExploded ? 'back.out(1.4)' : 'power2.inOut'
            });
        });

        // Roof lift
        if (roofGroup) {
            const totalFloors = floorGroups.length;
            const defaultRoofY = 0.4 + totalFloors * 2.4;
            const targetRoofY = isExploded ? (defaultRoofY + totalFloors * separationSpacing) : defaultRoofY;

            gsap.to(roofGroup.position, {
                y: targetRoofY,
                duration: 1.0,
                delay: totalFloors * 0.06,
                ease: isExploded ? 'back.out(1.4)' : 'power2.inOut'
            });
        }

        // Lift floating label accordingly
        if (labelSprite) {
            const defaultLabelY = (buildingGroup.userData.soTang || 3) * 2.4 + 4.6;
            const targetLabelY = isExploded ? (defaultLabelY + floorGroups.length * separationSpacing) : defaultLabelY;
            gsap.to(labelSprite.position, {
                y: targetLabelY,
                duration: 1.0,
                ease: 'power2.inOut'
            });
        }

        return isExploded;
    }

    // Highlight specific floor in building
    function highlightFloor(buildingGroup, floorIndex) {
        if (!buildingGroup || !buildingGroup.userData.floorGroups) return;
        const floorGroups = buildingGroup.userData.floorGroups;

        floorGroups.forEach((fl, idx) => {
            const fNum = idx + 1;
            const isTarget = (fNum === floorIndex);

            // Subtle color tint or scale
            gsap.to(fl.scale, {
                x: isTarget ? 1.04 : 1.0,
                z: isTarget ? 1.04 : 1.0,
                duration: 0.4,
                ease: 'power2.out'
            });
        });
    }

    return {
        flyToBuilding,
        resetToOverview,
        toggleExplodeFloors,
        highlightFloor,
        isExploded: () => isExploded
    };
})();

window.Animations3D = Animations3D;

