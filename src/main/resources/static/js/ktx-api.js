/**
 * KTX SMART HUB – HOLOGRAPHIC DIGITAL TWIN
 * ktx-api.js - Data & API Integration Service
 * Kết nối REST API thực tế: Khu, Tòa nhà, Tầng, Phòng, Thống kê
 */

const KtxApi = (function () {
    const STATE = {
        isDemo: false,
        apiConnected: false,
        lastUpdated: null,
        stats: null,
        zones: [],
        buildings: [],
        floors: [],
        rooms: [],
        campusHierarchy: [],
        callbacks: []
    };

    // Realistic Demo Dataset matching exact project SQL schema (used if MySQL is offline or empty)
    const DEMO_DATA = {
        stats: {
            totalStudents: 1250,
            totalBuildings: 5,
            totalRooms: 320,
            totalRoomTypes: 4,
            availableRooms: 48,
            occupiedRooms: 265,
            totalAccounts: 1300,
            totalContracts: 1250,
            activeContracts: 1180,
            totalInvoices: 3450,
            unpaidInvoices: 42,
            overdueInvoices: 8,
            totalRevenue: 2850000000,
            totalDebt: 95000000
        },
        zones: [
            { id: 1, maKhu: 'KHU-A', tenKhu: 'Khu A - Cơ sở chính', diaChi: 'Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội', moTa: 'Khuôn viên trung tâm KTX với đầy đủ tiện ích thể thao, căng-tin' },
            { id: 2, maKhu: 'KHU-B', tenKhu: 'Khu B - Mở rộng', diaChi: 'Khu Công Nghệ Cao, Hà Nội', moTa: 'Khuôn viên lưu trú chất lượng cao dành cho sinh viên quốc tế và nghiên cứu' }
        ],
        buildings: [
            { id: 1, khuId: 1, maToa: 'TOA-A1', tenToa: 'Tòa A1 - Ký túc xá Nam', soTang: 4, moTa: 'Tòa nhà lưu trú sinh viên Nam với phòng tập thể thao tầng trệt' },
            { id: 2, khuId: 1, maToa: 'TOA-A2', tenToa: 'Tòa A2 - Ký túc xá Nữ', soTang: 5, moTa: 'Tòa nhà sinh viên Nữ trang bị thẻ từ an ninh và phòng tự học 24/7' },
            { id: 3, khuId: 1, maToa: 'TOA-A3', tenToa: 'Tòa A3 - Chất lượng cao', soTang: 6, moTa: 'Tòa ký túc xá mô hình dịch vụ khép kín, điều hòa riêng từng phòng' },
            { id: 4, khuId: 2, maToa: 'TOA-B1', tenToa: 'Tòa B1 - Sinh viên Quốc tế', soTang: 4, moTa: 'Dành cho sinh viên trao đổi quốc tế và học viên cao học' },
            { id: 5, khuId: 2, maToa: 'TOA-B2', tenToa: 'Tòa B2 - Nghiên cứu sinh', soTang: 3, moTa: 'Khuôn viên yên tĩnh dành cho sinh viên năm cuối và nghiên cứu sinh' }
        ]
    };

    // Helper to generate procedural demo rooms & floors if demo mode
    function generateDemoFloorsAndRooms(buildings) {
        const floors = [];
        const rooms = [];
        let floorIdCounter = 1;
        let roomIdCounter = 1;

        buildings.forEach((b) => {
            const floorCount = b.soTang || 3;
            for (let f = 1; f <= floorCount; f++) {
                const floorObj = {
                    id: floorIdCounter++,
                    toaNhaId: b.id,
                    toaNha: b,
                    soTang: f
                };
                floors.push(floorObj);

                // 4 rooms per floor
                const roomsPerFloor = 4;
                for (let r = 1; r <= roomsPerFloor; r++) {
                    const roomNum = `${f}0${r}`;
                    const roomCode = `${b.maToa.replace('TOA-', '')}-${roomNum}`;
                    const isOccupied = (f + r) % 5 === 0;
                    const isMaint = (f + r) === 7;
                    const sucChua = (r % 2 === 0) ? 4 : (r === 3 ? 6 : 2);
                    const soNguoi = isOccupied ? sucChua : (isMaint ? 0 : Math.max(0, sucChua - (r % 3)));
                    const status = isMaint ? 'dang_sua' : (soNguoi >= sucChua ? 'da_day' : 'con_cho');
                    const gia = sucChua === 2 ? 1800000 : (sucChua === 4 ? 1200000 : 900000);

                    rooms.push({
                        id: roomIdCounter++,
                        tangId: floorObj.id,
                        tang: floorObj,
                        maPhong: roomCode,
                        sucChua: sucChua,
                        soNguoiHienTai: soNguoi,
                        loaiPhong: `${sucChua} người`,
                        giaPhong: gia,
                        trangThai: status
                    });
                }
            }
        });

        return { floors, rooms };
    }

    async function fetchJson(endpoint) {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 3500);
            const res = await fetch(endpoint, {
                headers: { 'Accept': 'application/json' },
                signal: controller.signal
            });
            clearTimeout(timeoutId);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return await res.json();
        } catch (err) {
            return null;
        }
    }

    // Load full digital twin hierarchy
    async function initData() {
        console.log('[KtxApi] Bắt đầu tải dữ liệu KTX từ hệ thống Backend...');

        const [statsData, khuData, toaNhaData, tangData, phongData] = await Promise.all([
            fetchJson('/api/dashboard/stats'),
            fetchJson('/api/khu'),
            fetchJson('/api/toa-nha'),
            fetchJson('/api/tang'),
            fetchJson('/api/phong')
        ]);

        const hasRealData = Boolean(toaNhaData && Array.isArray(toaNhaData) && toaNhaData.length > 0);

        if (hasRealData) {
            STATE.isDemo = false;
            STATE.apiConnected = true;
            STATE.stats = statsData || DEMO_DATA.stats;
            STATE.zones = (khuData && Array.isArray(khuData) && khuData.length > 0) ? khuData : DEMO_DATA.zones;
            STATE.buildings = toaNhaData;
            STATE.floors = (tangData && Array.isArray(tangData)) ? tangData : [];
            STATE.rooms = (phongData && Array.isArray(phongData)) ? phongData : [];
            console.log('[KtxApi] Đã kết nối thành công với MySQL & Spring Boot Backend! Tải được:', {
                buildings: STATE.buildings.length,
                floors: STATE.floors.length,
                rooms: STATE.rooms.length
            });
        } else {
            STATE.isDemo = true;
            STATE.apiConnected = false;
            STATE.stats = DEMO_DATA.stats;
            STATE.zones = DEMO_DATA.zones;
            STATE.buildings = DEMO_DATA.buildings;
            const demoGenerated = generateDemoFloorsAndRooms(STATE.buildings);
            STATE.floors = demoGenerated.floors;
            STATE.rooms = demoGenerated.rooms;
            console.warn('[KtxApi] Không thể kết nối hoặc Database chưa có bản ghi, chuyển sang chế độ mô phỏng số liệu chuẩn (Demo Mode).');
        }

        // Assemble Full Digital Twin Tree Hierarchy: Khu -> ToaNha -> Tang -> Phong
        buildHierarchy();
        STATE.lastUpdated = new Date();
        notifyListeners();
        return STATE;
    }

    function buildHierarchy() {
        const floorMap = new Map();
        STATE.floors.forEach((fl) => {
            const bId = fl.toaNha ? (fl.toaNha.id || fl.toaNha) : fl.toaNhaId;
            if (!floorMap.has(bId)) floorMap.set(bId, []);
            floorMap.get(bId).push({
                ...fl,
                rooms: []
            });
        });

        // Map rooms to floors
        STATE.rooms.forEach((rm) => {
            const fId = rm.tang ? (rm.tang.id || rm.tang) : rm.tangId;
            for (const flList of floorMap.values()) {
                const targetFloor = flList.find(f => f.id === fId);
                if (targetFloor) {
                    targetFloor.rooms.push(rm);
                    break;
                }
            }
        });

        // Assemble buildings with aggregated metrics
        STATE.campusHierarchy = STATE.buildings.map((b) => {
            const bId = b.id;
            let bFloors = floorMap.get(bId) || [];
            bFloors.sort((a, b) => (a.soTang || 0) - (b.soTang || 0));

            // If real database has buildings but no floor rows yet, synthesize floors based on b.soTang
            if (bFloors.length === 0) {
                const floorCount = b.soTang || 3;
                for (let f = 1; f <= floorCount; f++) {
                    bFloors.push({
                        id: `gen-${bId}-${f}`,
                        soTang: f,
                        toaNhaId: bId,
                        rooms: []
                    });
                }
            }

            // Aggregate metrics
            let totalRooms = 0;
            let availableRooms = 0;
            let occupiedRooms = 0;
            let totalCapacity = 0;
            let currentOccupants = 0;

            bFloors.forEach(f => {
                totalRooms += f.rooms.length;
                f.rooms.forEach(r => {
                    const cap = r.sucChua || 4;
                    const occ = r.soNguoiHienTai || 0;
                    totalCapacity += cap;
                    currentOccupants += occ;
                    if (r.trangThai === 'con_cho' || occ < cap) availableRooms++;
                    if (r.trangThai === 'da_day' || occ >= cap) occupiedRooms++;
                });
            });

            // Fallback metrics if empty rooms
            if (totalCapacity === 0) {
                totalCapacity = (b.soTang || 3) * 16;
                currentOccupants = Math.round(totalCapacity * 0.82);
                totalRooms = (b.soTang || 3) * 4;
                availableRooms = Math.max(1, totalRooms - Math.round(totalRooms * 0.82));
                occupiedRooms = totalRooms - availableRooms;
            }

            const occupancyRate = totalCapacity > 0 ? Math.round((currentOccupants / totalCapacity) * 100) : 85;

            return {
                ...b,
                floors: bFloors,
                metrics: {
                    totalRooms,
                    availableRooms,
                    occupiedRooms,
                    totalCapacity,
                    currentOccupants,
                    occupancyRate
                }
            };
        });
    }

    function subscribe(callback) {
        if (typeof callback === 'function') {
            STATE.callbacks.push(callback);
        }
    }

    function notifyListeners() {
        STATE.callbacks.forEach(cb => {
            try { cb(STATE); } catch (e) { console.error(e); }
        });
    }

    return {
        init: initData,
        getState: () => STATE,
        subscribe: subscribe,
        getBuildingById: (id) => STATE.campusHierarchy.find(b => b.id == id),
        getFloorsByBuilding: (bId) => {
            const b = STATE.campusHierarchy.find(x => x.id == bId);
            return b ? b.floors : [];
        },
        refresh: initData
    };
})();

// Export globally
window.KtxApi = KtxApi;

