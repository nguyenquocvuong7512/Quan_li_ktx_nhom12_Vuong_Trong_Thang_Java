/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * landing.js - Main Application Controller & UI Logic
 * Theme: AURORA GLASS – PREMIUM HOLOGRAPHIC CAMPUS 3D
 */

document.addEventListener('DOMContentLoaded', async () => {
    console.log('[Landing] Khởi chạy hệ thống KTX SMART HUB – AURORA GLASS 3D...');

    // 1. Initialize API & Data
    const apiState = await KtxApi.init();

    // 2. Update Live API Connection Badge
    updateApiStatusBadge(apiState);

    // 3. Update Hero & KPI Metrics Counters
    renderKpiMetrics(apiState.stats, apiState.campusHierarchy);

    // 4. Initialize Three.js 3D Viewport
    const canvasContainer = document.getElementById('scene3d-viewport');
    if (canvasContainer) {
        Scene3D.init(canvasContainer);
        Campus3D.buildCampus(apiState.campusHierarchy);
        Interactions3D.init(canvasContainer);
    }

    // 5. Populate Campus Explorer UI Controls
    populateBuildingQuickJump(apiState.campusHierarchy);
    setupCampusExplorerControls();

    // 6. Setup Interactive Hierarchy Switcher (KHU -> TOA -> TANG -> PHONG)
    setupHierarchyChain();

    // 7. Initialize GSAP Safe Scroll Animations
    setupSafeScrollAnimations();

    console.log('[Landing] Giao diện đã sẵn sàng!');
});

function updateApiStatusBadge(apiState) {
    const badgeEl = document.getElementById('api-status-badge');
    if (!badgeEl) return;

    if (apiState.apiConnected) {
        badgeEl.innerHTML = `
            <span class="live-dot pulse-green"></span>
            <span class="text-emerald-700 fw-bold">LIVE MYSQL CONNECTED</span>
        `;
        badgeEl.className = 'api-status-pill';
        badgeEl.style.borderColor = 'rgba(16, 185, 129, 0.4)';
    } else {
        badgeEl.innerHTML = `
            <span class="live-dot pulse-cyan"></span>
            <span class="text-sky-800 fw-bold">DEMO SIMULATION MODE</span>
        `;
        badgeEl.className = 'api-status-pill';
    }
}

function renderKpiMetrics(stats, campus) {
    if (!stats) return;

    const totalStudentsEl = document.getElementById('stat-total-students');
    const totalBuildingsEl = document.getElementById('stat-total-buildings');
    const totalRoomsEl = document.getElementById('stat-total-rooms');
    const availableRoomsEl = document.getElementById('stat-available-rooms');
    const occupancyRateEl = document.getElementById('stat-occupancy-rate');
    const occupancyFillEl = document.getElementById('stat-occupancy-fill');

    const totalBuildings = campus ? campus.length : (stats.totalBuildings || 5);
    let totalRooms = stats.totalRooms || 0;
    let availableRooms = stats.availableRooms || 0;

    if (campus && campus.length > 0) {
        let aggRooms = 0;
        let aggAvail = 0;
        campus.forEach(b => {
            if (b.metrics) {
                aggRooms += b.metrics.totalRooms;
                aggAvail += b.metrics.availableRooms;
            }
        });
        if (aggRooms > 0) {
            totalRooms = aggRooms;
            availableRooms = aggAvail;
        }
    }

    const occupied = totalRooms - availableRooms;
    const rate = totalRooms > 0 ? Math.round((occupied / totalRooms) * 100) : 88;

    animateCounter(totalStudentsEl, stats.totalStudents || 1250);
    animateCounter(totalBuildingsEl, totalBuildings);
    animateCounter(totalRoomsEl, totalRooms || 120);
    animateCounter(availableRoomsEl, availableRooms || 24);
    animateCounter(occupancyRateEl, rate, '%');

    if (occupancyFillEl) {
        occupancyFillEl.style.width = `${rate}%`;
    }

    // Also update HUD mini stats inside 3D viewport
    const hudStudents = document.getElementById('hud-students-val');
    const hudBuildings = document.getElementById('hud-buildings-val');
    const hudOccupancy = document.getElementById('hud-occupancy-val');
    if (hudStudents) hudStudents.textContent = Number(stats.totalStudents || 1250).toLocaleString('vi-VN');
    if (hudBuildings) hudBuildings.textContent = totalBuildings;
    if (hudOccupancy) hudOccupancy.textContent = `${rate}%`;
}

function animateCounter(el, targetVal, suffix = '') {
    if (!el) return;
    if (!window.gsap) {
        el.textContent = Number(targetVal).toLocaleString('vi-VN') + suffix;
        return;
    }

    const obj = { val: 0 };
    gsap.to(obj, {
        val: targetVal,
        duration: 1.6,
        ease: 'power2.out',
        onUpdate: () => {
            el.textContent = Math.round(obj.val).toLocaleString('vi-VN') + suffix;
        }
    });
}

function populateBuildingQuickJump(buildings) {
    const listEl = document.getElementById('quick-building-pills');
    if (!listEl || !buildings) return;

    listEl.innerHTML = '';

    buildings.slice(0, 4).forEach(b => {
        const btn = document.createElement('button');
        btn.className = 'quick-pill-btn';
        btn.innerHTML = `<i class="bi bi-building me-1"></i> ${b.maToa || b.tenToa}`;
        btn.addEventListener('click', () => {
            Interactions3D.selectBuildingById(b.id);
        });
        listEl.appendChild(btn);
    });
}

function setupCampusExplorerControls() {
    // Reset camera button
    const resetBtn = document.getElementById('btn-reset-camera');
    if (resetBtn) {
        resetBtn.addEventListener('click', () => {
            Interactions3D.resetOverview();
        });
    }

    // Toggle Explode All button
    const explodeAllBtn = document.getElementById('btn-explode-all');
    if (explodeAllBtn) {
        let allExploded = false;
        explodeAllBtn.addEventListener('click', () => {
            allExploded = !allExploded;
            const buildings = Campus3D.getBuildings();
            buildings.forEach(b => {
                Animations3D.toggleExplodeFloors(b, allExploded);
            });
            explodeAllBtn.innerHTML = allExploded
                ? `<i class="bi bi-box-arrow-in-down me-1"></i> Thu gọn`
                : `<i class="bi bi-box-arrow-up-right me-1"></i> Tách tầng`;
        });
    }

    // Reconnect API button
    const reconnectBtn = document.getElementById('btn-reconnect-api');
    if (reconnectBtn) {
        reconnectBtn.addEventListener('click', async () => {
            reconnectBtn.disabled = true;
            reconnectBtn.innerHTML = `<i class="bi bi-arrow-repeat spin me-1"></i> Đang tải...`;
            const newState = await KtxApi.refresh();
            updateApiStatusBadge(newState);
            renderKpiMetrics(newState.stats, newState.campusHierarchy);
            Campus3D.buildCampus(newState.campusHierarchy);
            populateBuildingQuickJump(newState.campusHierarchy);
            reconnectBtn.disabled = false;
            reconnectBtn.innerHTML = `<i class="bi bi-arrow-clockwise me-1"></i> Làm mới`;
        });
    }
}

// Interactive KHU -> TOA -> TANG -> PHONG Chain Switcher
function setupHierarchyChain() {
    const cards = document.querySelectorAll('.hierarchy-step-card');
    const previewTitle = document.getElementById('hierarchy-preview-title');
    const previewDesc = document.getElementById('hierarchy-preview-desc');
    const previewMeta = document.getElementById('hierarchy-preview-meta');

    const STEP_INFO = {
        khu: {
            title: 'Cấp Phân Khu (Campus / Zone)',
            desc: 'Phân định địa lý khuôn viên ký túc xá, quản lý hạ tầng giao thông nội bộ, khuôn viên cây xanh, tiện ích căng-tin và an ninh cổng.',
            meta: 'Bảng Database: khu &bull; Khóa: id, ma_khu, ten_khu &bull; Thể hiện 3D: Bệ Hologram bọc LED viền.'
        },
        toa: {
            title: 'Cấp Tòa Nhà Kiến Trúc (Building)',
            desc: 'Mỗi tòa nhà được dựng hình 3D thực với số tầng tương ứng trường soTang trong cơ sở dữ liệu. Nhãn thông tin lơ lửng hiển thị tình trạng cư dân và tỷ lệ lấp đầy.',
            meta: 'Bảng Database: toa_nha &bull; Khóa: id, khu_id, ma_toa, so_tang &bull; Thể hiện 3D: Khối kiến trúc composite kính trắng xanh.'
        },
        tang: {
            title: 'Cấp Tầng Lầu (Floor Layer)',
            desc: 'Các tầng có thể tách rời trực quan (Explode Floors). Cho phép ban quản lý quan sát sơ đồ bố trí không gian, hành lang và cửa sổ phòng từng tầng.',
            meta: 'Bảng Database: tang &bull; Khóa: id, toa_nha_id, so_tang &bull; Thể hiện 3D: Khối tầng phân lớp có thể nâng hạ bằng GSAP.'
        },
        phong: {
            title: 'Cấp Phòng Lưu Trú (Dorm Room)',
            desc: 'Đơn vị quản lý cơ sở nhỏ nhất nhưng quan trọng nhất. Mỗi phòng hiển thị sức chứa, số người ở thực tế, loại phòng và đơn giá hàng tháng.',
            meta: 'Bảng Database: phong &bull; Khóa: id, tang_id, ma_phong, suc_chua, so_nguoi_hien_tai &bull; Thể hiện 3D: Chip phòng mã màu trực quan.'
        }
    };

    cards.forEach(card => {
        card.addEventListener('click', () => {
            const step = card.getAttribute('data-step');
            cards.forEach(c => c.classList.remove('active'));
            card.classList.add('active');

            const info = STEP_INFO[step];
            if (info) {
                if (previewTitle) previewTitle.textContent = info.title;
                if (previewDesc) previewDesc.textContent = info.desc;
                if (previewMeta) previewMeta.innerHTML = info.meta;
            }

            // Trigger corresponding 3D focus
            if (window.Interactions3D && typeof window.Interactions3D.focusHierarchyStep === 'function') {
                window.Interactions3D.focusHierarchyStep(step);
            }
        });
    });
}

// Safe GSAP Animations - Never leaves content hidden!
function setupSafeScrollAnimations() {
    if (!window.gsap) return;

    // Subtle Hero Fade In
    gsap.from('.hero-content > *', {
        y: 20,
        opacity: 0,
        stagger: 0.08,
        duration: 0.8,
        ease: 'power2.out',
        clearProps: 'all' // Crucial: clearProps ensures no leftover opacity: 0
    });

    if (window.ScrollTrigger) {
        try {
            gsap.registerPlugin(ScrollTrigger);

            // Safe reveal on KPI cards
            gsap.from('.kpi-card', {
                scrollTrigger: {
                    trigger: '#overview-stats',
                    start: 'top 90%',
                    toggleActions: 'play none none none'
                },
                y: 25,
                opacity: 0,
                stagger: 0.07,
                duration: 0.6,
                ease: 'power2.out',
                clearProps: 'all'
            });

            // Safe reveal on Bento Cards
            gsap.from('.bento-card', {
                scrollTrigger: {
                    trigger: '#features-section',
                    start: 'top 85%',
                    toggleActions: 'play none none none'
                },
                y: 30,
                opacity: 0,
                stagger: 0.08,
                duration: 0.7,
                ease: 'power2.out',
                clearProps: 'all'
            });

            // Refresh ScrollTrigger after slight layout stabilization
            setTimeout(() => {
                ScrollTrigger.refresh();
            }, 500);
        } catch (e) {
            console.warn('[Landing] ScrollTrigger setup warning:', e);
        }
    }
}
