/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * interactions3d.js - Raycasting, Hover Effects, Tooltips, Building & Room Inspections
 */

const Interactions3D = (function () {
    let raycaster = new THREE.Raycaster();
    let mouse = new THREE.Vector2();
    let hoveredBuilding = null;
    let selectedBuilding = null;
    let selectedFloorIndex = 1;
    let tooltipEl = null;

    function init(containerElement) {
        if (!containerElement) return;

        createTooltipElement();

        const domEl = containerElement.querySelector('canvas') || containerElement;
        domEl.addEventListener('mousemove', onMouseMove, false);
        domEl.addEventListener('click', onClick, false);
        domEl.addEventListener('mouseleave', onMouseLeave, false);

        console.log('[Interactions3D] Khởi tạo hệ thống tương tác Raycaster & Tooltip thành công!');
    }

    function createTooltipElement() {
        tooltipEl = document.getElementById('hologram-tooltip');
        if (!tooltipEl) {
            tooltipEl = document.createElement('div');
            tooltipEl.id = 'hologram-tooltip';
            tooltipEl.className = 'hologram-tooltip';
            document.body.appendChild(tooltipEl);
        }
    }

    function onMouseMove(event) {
        const rect = event.currentTarget.getBoundingClientRect();
        mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

        checkIntersection(event.clientX, event.clientY);
    }

    function onMouseLeave() {
        if (hoveredBuilding) {
            setBuildingHover(hoveredBuilding, false);
            hoveredBuilding = null;
        }
        hideTooltip();
    }

    function checkIntersection(clientX, clientY) {
        const camera = Scene3D.getCamera();
        const buildings = Campus3D.getBuildings();
        if (!camera || !buildings || buildings.length === 0) return;

        raycaster.setFromCamera(mouse, camera);
        const intersects = raycaster.intersectObjects(buildings, true);

        if (intersects.length > 0) {
            let hitObj = intersects[0].object;
            while (hitObj.parent && hitObj.userData.type !== 'building' && hitObj.parent !== Scene3D.getScene()) {
                hitObj = hitObj.parent;
            }

            if (hitObj && hitObj.userData && hitObj.userData.type === 'building') {
                if (hoveredBuilding !== hitObj) {
                    if (hoveredBuilding) setBuildingHover(hoveredBuilding, false);
                    hoveredBuilding = hitObj;
                    setBuildingHover(hoveredBuilding, true);
                }
                showTooltip(hoveredBuilding, clientX, clientY);
                document.body.style.cursor = 'pointer';
                return;
            }
        }

        if (hoveredBuilding) {
            setBuildingHover(hoveredBuilding, false);
            hoveredBuilding = null;
        }
        hideTooltip();
        document.body.style.cursor = 'default';
    }

    function setBuildingHover(buildingGroup, isHovered) {
        const floorGroups = buildingGroup.userData.floorGroups;
        if (!floorGroups) return;

        floorGroups.forEach(fl => {
            fl.children.forEach(mesh => {
                if (mesh.isMesh && mesh.material && mesh.material.emissive) {
                    if (isHovered) {
                        mesh.material.emissive.setHex(0x00C8F8);
                        mesh.material.emissiveIntensity = 0.4;
                    } else {
                        mesh.material.emissive.setHex(0x000000);
                        mesh.material.emissiveIntensity = 0.0;
                    }
                }
            });
        });
    }

    function showTooltip(buildingGroup, x, y) {
        if (!tooltipEl) return;
        const b = buildingGroup.userData.data || buildingGroup.userData;
        const metrics = b.metrics || {
            totalRooms: (b.soTang || 3) * 4,
            availableRooms: 6,
            occupancyRate: 85
        };

        tooltipEl.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-1">
                <span class="badge bg-sky-100 text-sky-700 font-monospace px-2 py-0.5 rounded">${b.maToa || 'TÒA'}</span>
                <span class="small fw-bold text-muted">${b.soTang || 3} Tầng</span>
            </div>
            <div class="fw-bold fs-6 text-dark mb-2">${b.tenToa || 'Tòa Ký Túc Xá'}</div>
            <div class="small d-flex flex-column gap-1 mb-2">
                <div class="d-flex justify-content-between"><span class="text-muted">Tổng phòng:</span> <strong>${metrics.totalRooms} phòng</strong></div>
                <div class="d-flex justify-content-between"><span class="text-muted">Còn trống:</span> <strong class="text-sky-600">${metrics.availableRooms} phòng</strong></div>
                <div class="d-flex justify-content-between"><span class="text-muted">Tỷ lệ lấp đầy:</span> <strong class="text-primary">${metrics.occupancyRate}%</strong></div>
            </div>
            <div class="small text-sky-600 fw-bold border-top pt-1"><i class="bi bi-cursor-fill me-1"></i> Nhấp để xem tầng & phòng</div>
        `;

        tooltipEl.style.left = `${x + 16}px`;
        tooltipEl.style.top = `${y + 16}px`;
        tooltipEl.classList.add('visible');
    }

    function hideTooltip() {
        if (tooltipEl) {
            tooltipEl.classList.remove('visible');
        }
    }

    function onClick(event) {
        if (hoveredBuilding) {
            selectBuilding(hoveredBuilding);
        }
    }

    function selectBuilding(buildingGroup) {
        selectedBuilding = buildingGroup;
        selectedFloorIndex = 1;
        hideTooltip();

        // 1. Camera fly to building
        Animations3D.flyToBuilding(buildingGroup, () => {});

        // 2. Open Inspector Panel
        renderBuildingInspectionPanel(buildingGroup);

        // 3. Dispatch event
        window.dispatchEvent(new CustomEvent('ktx:buildingSelected', {
            detail: buildingGroup.userData
        }));
    }

    function selectBuildingById(buildingId) {
        const b = Campus3D.getBuildingById(buildingId);
        if (b) {
            selectBuilding(b);
        }
    }

    function renderBuildingInspectionPanel(buildingGroup) {
        const bData = buildingGroup.userData.data || buildingGroup.userData;
        const bMetrics = bData.metrics || {
            totalRooms: (bData.soTang || 3) * 4,
            availableRooms: 6,
            occupiedRooms: 18,
            totalCapacity: 80,
            currentOccupants: 68,
            occupancyRate: 85
        };

        const panel = document.getElementById('building-inspection-panel');
        if (!panel) return;

        const floors = bData.floors || [];
        const floorCount = bData.soTang || floors.length || 3;

        let floorButtonsHtml = '';
        for (let f = 1; f <= floorCount; f++) {
            const activeClass = (f === selectedFloorIndex) ? 'btn-primary text-white' : 'btn-light border';
            floorButtonsHtml += `
                <button class="btn btn-sm ${activeClass} rounded-pill px-3 py-1 fw-bold floor-pill-btn" onclick="Interactions3D.selectFloor(${f})">
                    Tầng ${f}
                </button>
            `;
        }

        panel.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom">
                <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-sky-100 text-sky-700 font-monospace">DIGITAL TWIN</span>
                    <strong class="text-dark">${bData.maToa || 'TOA'}</strong>
                </div>
                <button type="button" class="btn-close" onclick="Interactions3D.closeInspectionPanel()"></button>
            </div>

            <div class="mb-3">
                <h5 class="fw-bold text-dark mb-1">${bData.tenToa || 'Tòa Nhà KTX'}</h5>
                <p class="text-muted small mb-2">${bData.moTa || 'Khuôn viên lưu trú thông minh với hệ thống giám sát 24/7.'}</p>

                <div class="p-2.5 bg-sky-50 rounded-xl border border-sky-100 mb-2">
                    <div class="d-flex justify-content-between small fw-bold mb-1">
                        <span>Lấp đầy</span>
                        <span class="text-primary">${bMetrics.occupancyRate}%</span>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" style="width: ${bMetrics.occupancyRate}%;"></div>
                    </div>
                    <div class="d-flex justify-content-between text-muted small mt-2">
                        <span>Tổng: <strong>${bMetrics.totalRooms}</strong> phòng</span>
                        <span>Trống: <strong class="text-sky-600">${bMetrics.availableRooms}</strong></span>
                        <span>Sinh viên: <strong>${bMetrics.currentOccupants}</strong></span>
                    </div>
                </div>

                <div class="small fw-bold text-dark mb-1.5"><i class="bi bi-layers text-cyan me-1"></i>Chọn tầng khảo sát:</div>
                <div class="d-flex gap-1.5 flex-wrap mb-3">
                    ${floorButtonsHtml}
                </div>

                <div id="floor-rooms-container"></div>

                <div class="d-flex flex-column gap-2 mt-3 pt-2 border-top">
                    <button class="btn btn-sm btn-outline-primary rounded-pill fw-bold" id="btn-explode-floors" onclick="Interactions3D.toggleExplode()">
                        <i class="bi bi-box-arrow-up-right me-1"></i> <span id="explode-btn-text">Tách tầng 3D (Explode)</span>
                    </button>
                    <div class="d-flex gap-2">
                        <a href="/index.html" class="btn btn-sm btn-primary rounded-pill fw-bold flex-fill">
                            <i class="bi bi-speedometer2 me-1"></i> Mở Dashboard
                        </a>
                        <button class="btn btn-sm btn-light border rounded-pill px-3" onclick="Interactions3D.resetOverview()" title="Toàn cảnh">
                            <i class="bi bi-arrows-angle-contract"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;

        panel.classList.add('active');
        renderFloorRooms(selectedFloorIndex);
    }

    function selectFloor(floorNum) {
        selectedFloorIndex = floorNum;

        const buttons = document.querySelectorAll('.floor-pill-btn');
        buttons.forEach((btn, idx) => {
            if (idx + 1 === floorNum) {
                btn.className = 'btn btn-sm btn-primary text-white rounded-pill px-3 py-1 fw-bold floor-pill-btn';
            } else {
                btn.className = 'btn btn-sm btn-light border rounded-pill px-3 py-1 fw-bold floor-pill-btn';
            }
        });

        if (selectedBuilding) {
            Animations3D.highlightFloor(selectedBuilding, floorNum);
        }

        renderFloorRooms(floorNum);
    }

    function renderFloorRooms(floorNum) {
        const container = document.getElementById('floor-rooms-container');
        if (!container || !selectedBuilding) return;

        const bData = selectedBuilding.userData.data || selectedBuilding.userData;
        const floors = bData.floors || [];
        const currentFloor = floors[floorNum - 1];

        const rooms = (currentFloor && currentFloor.rooms && currentFloor.rooms.length > 0)
            ? currentFloor.rooms
            : generateFallbackRooms(bData.maToa, floorNum);

        let roomsHtml = `
            <div class="d-flex justify-content-between align-items-center small text-muted mb-2">
                <span>Phòng Tầng ${floorNum}</span>
                <span class="badge bg-light text-dark border font-monospace">${rooms.length} phòng</span>
            </div>
            <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.45rem;">
        `;

        rooms.forEach(r => {
            const isConCho = (r.trangThai === 'con_cho' || r.soNguoiHienTai < r.sucChua);
            const isDangSua = (r.trangThai === 'dang_sua');
            const borderCol = isDangSua ? '#EF4444' : (isConCho ? '#10B981' : '#F59E0B');
            const statusTxt = isDangSua ? 'Bảo trì' : (isConCho ? 'Còn chỗ' : 'Đã đầy');

            roomsHtml += `
                <div class="p-2 bg-white rounded-xl border cursor-pointer hover:border-sky-400 transition" 
                     style="border-left: 3.5px solid ${borderCol}; box-shadow: 0 1px 3px rgba(0,0,0,0.04);" 
                     onclick="Interactions3D.showRoomDetails(${JSON.stringify(r).replace(/"/g, '&quot;')})">
                    <div class="fw-bold font-monospace text-dark" style="font-size: 0.82rem;">${r.maPhong}</div>
                    <div class="d-flex justify-content-between small text-muted" style="font-size: 0.7rem;">
                        <span><i class="bi bi-person"></i> ${r.soNguoiHienTai || 0}/${r.sucChua || 4}</span>
                        <span style="color: ${borderCol}; font-weight: 700;">${statusTxt}</span>
                    </div>
                </div>
            `;
        });

        roomsHtml += '</div>';
        container.innerHTML = roomsHtml;
    }

    function generateFallbackRooms(maToa, floorNum) {
        const list = [];
        const prefix = (maToa || 'A1').replace('TOA-', '');
        for (let i = 1; i <= 4; i++) {
            const cap = (i % 2 === 0) ? 4 : 2;
            const occ = (i === 1) ? cap : Math.max(0, cap - 1);
            list.push({
                id: `gen-${floorNum}-${i}`,
                maPhong: `${prefix}-${floorNum}0${i}`,
                sucChua: cap,
                soNguoiHienTai: occ,
                loaiPhong: `${cap} người`,
                giaPhong: cap === 2 ? 1800000 : 1200000,
                trangThai: occ >= cap ? 'da_day' : 'con_cho'
            });
        }
        return list;
    }

    function showRoomDetails(roomData) {
        const modal = document.getElementById('room-detail-modal');
        if (!modal) return;

        const isConCho = (roomData.trangThai === 'con_cho' || roomData.soNguoiHienTai < roomData.sucChua);
        const statusBadge = isConCho
            ? '<span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-1 rounded-pill"><i class="bi bi-check-circle-fill me-1"></i>Còn chỗ lưu trú</span>'
            : '<span class="badge bg-warning-subtle text-warning border border-warning-subtle px-3 py-1 rounded-pill"><i class="bi bi-people-fill me-1"></i>Đã đủ số người</span>';

        const priceFormatted = (roomData.giaPhong != null)
            ? Number(roomData.giaPhong).toLocaleString('vi-VN') + ' ₫ / tháng'
            : '1.200.000 ₫ / tháng';

        modal.innerHTML = `
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-2xl rounded-3xl" style="background: rgba(255,255,255,0.96); backdrop-filter: blur(20px); border: 1px solid rgba(0,200,248,0.45);">
                    <div class="modal-header border-0 pb-0">
                        <div class="d-flex align-items-center gap-2">
                            <span class="badge bg-sky-100 text-sky-700 font-monospace px-2.5 py-1">PHÒNG KTX</span>
                            <h5 class="modal-title fw-bold text-dark mb-0">${roomData.maPhong}</h5>
                        </div>
                        <button type="button" class="btn-close" onclick="Interactions3D.closeRoomDetails()"></button>
                    </div>
                    <div class="modal-body py-4">
                        <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
                            <div>
                                <span class="text-secondary small d-block">Trạng thái</span>
                                ${statusBadge}
                            </div>
                            <div class="text-end">
                                <span class="text-secondary small d-block">Giá niêm yết</span>
                                <strong class="fs-5 text-primary">${priceFormatted}</strong>
                            </div>
                        </div>

                        <div class="row g-3">
                            <div class="col-6">
                                <div class="p-3 rounded-2xl bg-slate-50 border">
                                    <span class="text-muted small d-block">Sức chứa</span>
                                    <strong class="fs-6 text-dark">${roomData.sucChua} Sinh viên</strong>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 rounded-2xl bg-slate-50 border">
                                    <span class="text-muted small d-block">Đang ở</span>
                                    <strong class="fs-6 text-dark">${roomData.soNguoiHienTai} Sinh viên</strong>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 rounded-2xl bg-slate-50 border">
                                    <span class="text-muted small d-block">Loại phòng</span>
                                    <strong class="fs-6 text-dark">${roomData.loaiPhong || 'Chuẩn KTX'}</strong>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 rounded-2xl bg-slate-50 border">
                                    <span class="text-muted small d-block">Trang bị</span>
                                    <strong class="fs-6 text-dark">Điều hòa, Nước nóng</strong>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer border-0 pt-0">
                        <a href="/dang-ky-phong.html" class="btn btn-primary rounded-pill px-4 fw-bold">
                            <i class="bi bi-clipboard2-check me-1"></i> Đăng ký phòng này
                        </a>
                        <button type="button" class="btn btn-light rounded-pill px-4 border" onclick="Interactions3D.closeRoomDetails()">Đóng</button>
                    </div>
                </div>
            </div>
        `;

        modal.classList.add('show');
        modal.style.display = 'block';
    }

    function closeRoomDetails() {
        const modal = document.getElementById('room-detail-modal');
        if (modal) {
            modal.classList.remove('show');
            modal.style.display = 'none';
        }
    }

    function toggleExplode() {
        if (!selectedBuilding) return;
        const isNowExploded = Animations3D.toggleExplodeFloors(selectedBuilding);
        const btnText = document.getElementById('explode-btn-text');
        if (btnText) {
            btnText.textContent = isNowExploded ? 'Thu gọn tầng (Collapse)' : 'Tách tầng 3D (Explode)';
        }
    }

    function resetOverview() {
        if (selectedBuilding && selectedBuilding.userData.isExploded) {
            Animations3D.toggleExplodeFloors(selectedBuilding, false);
        }
        Animations3D.resetToOverview(() => {
            closeInspectionPanel();
        });
    }

    function closeInspectionPanel() {
        const panel = document.getElementById('building-inspection-panel');
        if (panel) panel.classList.remove('active');
        selectedBuilding = null;
    }

    // Interactive link from Section 3 (KHU -> TOA -> TANG -> PHONG)
    function focusHierarchyStep(stepName) {
        // Scroll smoothly to 3D Viewport
        const viewport = document.getElementById('scene3d-viewport');
        if (viewport) {
            viewport.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }

        const buildings = Campus3D.getBuildings();
        if (!buildings || buildings.length === 0) return;

        if (stepName === 'khu') {
            resetOverview();
        } else if (stepName === 'toa') {
            selectBuilding(buildings[0]);
        } else if (stepName === 'tang') {
            selectBuilding(buildings[0]);
            setTimeout(() => {
                Animations3D.toggleExplodeFloors(buildings[0], true);
            }, 600);
        } else if (stepName === 'phong') {
            selectBuilding(buildings[0]);
            selectFloor(1);
        }
    }

    return {
        init,
        selectBuilding,
        selectBuildingById,
        selectFloor,
        showRoomDetails,
        closeRoomDetails,
        toggleExplode,
        resetOverview,
        closeInspectionPanel,
        focusHierarchyStep
    };
})();

window.Interactions3D = Interactions3D;
