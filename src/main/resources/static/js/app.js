/**
 * Hệ Thống Quản Lý Ký Túc Xá Sinh Viên
 * app.js - Unified Client JavaScript Library with JWT & RBAC
 */

const App = {
    // Current user state & RBAC
    auth: {
        getUser() {
            try {
                const raw = localStorage.getItem('ktx_user');
                return raw ? JSON.parse(raw) : null;
            } catch (e) {
                return null;
            }
        },
        setUser(user) {
            localStorage.setItem('ktx_user', JSON.stringify(user));
        },
        getToken() {
            const user = this.getUser();
            return user ? user.token : null;
        },
        logout() {
            localStorage.removeItem('ktx_user');
            window.location.href = '/login.html';
        },
        hasRole(roleName) {
            const user = this.getUser();
            if (!user || !user.roles) return false;
            const target = roleName.toUpperCase().replace('ROLE_', '');
            return user.roles.some(r => r.toUpperCase().replace('ROLE_', '') === target);
        },
        isAdmin() {
            return this.hasRole('ADMIN') || this.hasRole('QUAN_TRI');
        },
        isManager() {
            return this.hasRole('QUAN_LY') || this.hasRole('QUAN_LY_KY_TUC');
        },
        isStaff() {
            return this.isAdmin() || this.isManager();
        },
        isStudent() {
            return this.hasRole('SINH_VIEN');
        },
        requireAuth(allowedRoles = []) {
            const user = this.getUser();
            const currentPath = window.location.pathname;

            if (!user && !currentPath.endsWith('login.html') && !currentPath.endsWith('/login') && !currentPath.endsWith('landing.html')) {
                window.location.href = '/login.html';
                return null;
            }

            if (user && allowedRoles && allowedRoles.length > 0) {
                const hasPermission = allowedRoles.some(r => this.hasRole(r));
                if (!hasPermission) {
                    alert('Bạn không có quyền truy cập vào trang này (Chức năng dành cho Quản trị viên/Quản lý)!');
                    window.location.href = this.isStudent() ? '/hoa-don.html' : '/index.html';
                    return null;
                }
            }

            return user;
        }
    },

    // HTTP API Request wrapper with JWT Authorization Header
    async api(url, options = {}) {
        const token = this.auth.getToken();
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
            config.body = JSON.stringify(config.body);
        }

        try {
            const res = await fetch(url, config);
            let data = null;
            const contentType = res.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                data = await res.json();
            } else {
                const text = await res.text();
                try {
                    data = JSON.parse(text);
                } catch {
                    data = text;
                }
            }

            if (!res.ok) {
                // Xử lý hết hạn token hoặc không có quyền
                if (res.status === 401) {
                    if (!window.location.pathname.endsWith('login.html')) {
                        App.toast('Phiên làm việc đã hết hạn hoặc chưa đăng nhập. Vui lòng đăng nhập lại!', 'warning');
                        setTimeout(() => {
                            App.auth.logout();
                        }, 1200);
                    }
                } else if (res.status === 403) {
                    App.toast('Bạn không có quyền thực hiện thao tác này (403 Forbidden)!', 'danger');
                }

                let errorMsg = 'Có lỗi xảy ra khi xử lý yêu cầu';
                if (data && typeof data === 'object') {
                    errorMsg = data.detail || data.message || data.error || data.title || JSON.stringify(data);
                }
                return { success: false, status: res.status, error: errorMsg, data };
            }

            return { success: true, status: res.status, data };
        } catch (err) {
            console.error('Fetch API error:', err);
            return {
                success: false,
                status: 0,
                error: 'Không thể kết nối đến máy chủ Backend (vui lòng kiểm tra kết nối mạng và MySQL / Spring Boot)!'
            };
        }
    },

    // Toast Notifications
    toast(message, type = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.zIndex = '99999';
            document.body.appendChild(container);
        }

        const toastId = 'toast-' + Date.now();
        const icon = type === 'success' ? 'bi-check-circle-fill text-success'
            : type === 'danger' || type === 'error' ? 'bi-exclamation-triangle-fill text-danger'
            : type === 'warning' ? 'bi-exclamation-circle-fill text-warning'
            : 'bi-info-circle-fill text-primary';

        const title = type === 'success' ? 'Thành công'
            : type === 'danger' || type === 'error' ? 'Lỗi'
            : type === 'warning' ? 'Cảnh báo'
            : 'Thông báo';

        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center shadow-lg border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body d-flex align-items-center gap-2">
                        <i class="bi ${icon} fs-5"></i>
                        <div>
                            <strong class="d-block">${title}</strong>
                            <span class="text-secondary small">${message}</span>
                        </div>
                    </div>
                    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', toastHtml);
        const el = document.getElementById(toastId);
        const bsToast = new bootstrap.Toast(el, { delay: 4000 });
        bsToast.show();
        el.addEventListener('hidden.bs.toast', () => el.remove());
    },

    // Dynamic Confirm Modal
    confirm({ title = 'Xác nhận xóa', message = 'Bạn có chắc chắn muốn thực hiện hành động này?', confirmText = 'Xóa', btnClass = 'btn-danger' }) {
        return new Promise((resolve) => {
            let modalEl = document.getElementById('app-confirm-modal');
            if (!modalEl) {
                const html = `
                <div class="modal fade" id="app-confirm-modal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg" style="border-radius: 14px;">
                            <div class="modal-header border-bottom-0 pb-1">
                                <h5 class="modal-title fs-6 fw-bold" id="confirm-modal-title">Xác nhận</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body py-3" id="confirm-modal-body">
                                Nội dung xác nhận
                            </div>
                            <div class="modal-footer border-top-0 pt-0">
                                <button type="button" class="btn btn-sm btn-light border px-3" data-bs-dismiss="modal">Hủy bỏ</button>
                                <button type="button" class="btn btn-sm px-3" id="confirm-modal-btn">Đồng ý</button>
                            </div>
                        </div>
                    </div>
                </div>`;
                document.body.insertAdjacentHTML('beforeend', html);
                modalEl = document.getElementById('app-confirm-modal');
            }

            document.getElementById('confirm-modal-title').textContent = title;
            document.getElementById('confirm-modal-body').textContent = message;
            const confirmBtn = document.getElementById('confirm-modal-btn');
            confirmBtn.className = `btn btn-sm ${btnClass} px-3`;
            confirmBtn.textContent = confirmText;

            const modal = new bootstrap.Modal(modalEl);

            const handleConfirm = () => {
                confirmBtn.removeEventListener('click', handleConfirm);
                modal.hide();
                resolve(true);
            };

            const handleHidden = () => {
                confirmBtn.removeEventListener('click', handleConfirm);
                modalEl.removeEventListener('hidden.bs.modal', handleHidden);
                resolve(false);
            };

            confirmBtn.addEventListener('click', handleConfirm);
            modalEl.addEventListener('hidden.bs.modal', handleHidden, { once: true });
            modal.show();
        });
    },

    // Format VND Currency
    formatMoney(amount) {
        if (amount == null) return '0 ₫';
        return Number(amount).toLocaleString('vi-VN') + ' ₫';
    },

    // Format Date
    formatDate(dateStr) {
        if (!dateStr) return '-';
        try {
            const d = new Date(dateStr);
            if (isNaN(d.getTime())) return dateStr;
            return d.toLocaleDateString('vi-VN');
        } catch {
            return dateStr;
        }
    },

    // Render Unified Pagination Controls
    renderPagination(containerId, pageData, onPageChange) {
        const container = document.getElementById(containerId);
        if (!container || !pageData) return;

        const { currentPage, totalPages, totalElements, pageSize, first, last } = pageData;
        if (totalElements === 0) {
            container.innerHTML = '<div class="text-muted small py-2">Không có bản ghi nào</div>';
            return;
        }

        const fromRecord = currentPage * pageSize + 1;
        const toRecord = Math.min((currentPage + 1) * pageSize, totalElements);

        let pageItems = '';
        const maxVisiblePages = 5;
        let startPage = Math.max(0, currentPage - 2);
        let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);
        if (endPage - startPage < maxVisiblePages - 1) {
            startPage = Math.max(0, endPage - maxVisiblePages + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const activeClass = i === currentPage ? 'active' : '';
            pageItems += `<li class="page-item ${activeClass}"><button type="button" class="page-link" onclick="window._changePage(${i})">${i + 1}</button></li>`;
        }

        container.innerHTML = `
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-center gap-2 py-2 px-1">
                <div class="small text-muted">
                    Hiển thị <strong>${fromRecord} - ${toRecord}</strong> trên tổng <strong>${totalElements}</strong> bản ghi (Trang ${currentPage + 1}/${totalPages})
                </div>
                <div class="d-flex align-items-center gap-2">
                    <select class="form-select form-select-sm w-auto" onchange="window._changePageSize(this.value)" title="Số dòng mỗi trang">
                        <option value="10" ${pageSize === 10 ? 'selected' : ''}>10 dòng</option>
                        <option value="20" ${pageSize === 20 ? 'selected' : ''}>20 dòng</option>
                        <option value="50" ${pageSize === 50 ? 'selected' : ''}>50 dòng</option>
                    </select>
                    <nav aria-label="Page navigation">
                        <ul class="pagination pagination-sm mb-0">
                            <li class="page-item ${first ? 'disabled' : ''}">
                                <button type="button" class="page-link" onclick="window._changePage(0)" title="Trang đầu">&laquo;</button>
                            </li>
                            <li class="page-item ${first ? 'disabled' : ''}">
                                <button type="button" class="page-link" onclick="window._changePage(${currentPage - 1})" title="Trang trước">&lsaquo;</button>
                            </li>
                            ${pageItems}
                            <li class="page-item ${last ? 'disabled' : ''}">
                                <button type="button" class="page-link" onclick="window._changePage(${currentPage + 1})" title="Trang sau">&rsaquo;</button>
                            </li>
                            <li class="page-item ${last ? 'disabled' : ''}">
                                <button type="button" class="page-link" onclick="window._changePage(${totalPages - 1})" title="Trang cuối">&raquo;</button>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        `;

        window._changePage = (p) => {
            if (p >= 0 && p < totalPages && p !== currentPage) {
                onPageChange(p, pageSize);
            }
        };

        window._changePageSize = (sz) => {
            const newSize = parseInt(sz);
            if (newSize > 0) {
                onPageChange(0, newSize);
            }
        };
    },

    // Initialize layout, topbar, active sidebar menu, RBAC menu filtering
    initLayout(activeMenu, requiredRoles = []) {
        // Enforce page authorization
        const user = this.auth.requireAuth(requiredRoles);

        // Toggle sidebar on mobile
        const toggleBtn = document.getElementById('sidebar-toggle');
        const sidebar = document.getElementById('sidebar');
        if (toggleBtn && sidebar) {
            toggleBtn.addEventListener('click', () => {
                sidebar.classList.toggle('show');
            });
        }

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (window.innerWidth < 992 && sidebar && sidebar.classList.contains('show')) {
                if (!sidebar.contains(e.target) && (!toggleBtn || !toggleBtn.contains(e.target))) {
                    sidebar.classList.remove('show');
                }
            }
        });

        // Set active link
        if (activeMenu) {
            const links = document.querySelectorAll('.sidebar-link');
            links.forEach(l => {
                if (l.getAttribute('data-menu') === activeMenu) {
                    l.classList.add('active');
                } else {
                    l.classList.remove('active');
                }
            });
        }

        // Display current user & Role Badge
        const userNameEl = document.getElementById('topbar-user-name');
        const userRoleEl = document.getElementById('topbar-user-role');
        const userAvatarEl = document.getElementById('topbar-user-avatar');

        if (userNameEl) {
            userNameEl.textContent = user ? user.hoTen : 'Khách';
        }

        if (userRoleEl) {
            if (this.auth.isAdmin()) {
                userRoleEl.innerHTML = '<span class="badge bg-danger-subtle text-danger border border-danger-subtle fw-semibold px-2 py-0.5" style="font-size: 0.68rem;">ADMINISTRATOR</span>';
            } else if (this.auth.isManager()) {
                userRoleEl.innerHTML = '<span class="badge bg-primary-subtle text-primary border border-primary-subtle fw-semibold px-2 py-0.5" style="font-size: 0.68rem;">QUẢN LÝ KTX</span>';
            } else if (this.auth.isStudent()) {
                userRoleEl.innerHTML = '<span class="badge bg-success-subtle text-success border border-success-subtle fw-semibold px-2 py-0.5" style="font-size: 0.68rem;">SINH VIÊN (' + (user.maSinhVien || user.maNguoiDung) + ')</span>';
            } else {
                userRoleEl.textContent = user ? user.maNguoiDung : 'KTX';
            }
        }

        if (userAvatarEl) {
            const initial = user && user.hoTen ? user.hoTen.trim().charAt(0).toUpperCase() : 'U';
            userAvatarEl.textContent = initial;
        }

        // Apply RBAC UI filtering on sidebar & action buttons
        this.applyRbacUi(user);
    },

    // Filter UI elements dynamically based on user roles
    applyRbacUi(user) {
        if (!user) return;

        const isStudent = this.auth.isStudent();
        const isManager = this.auth.isManager();
        const isAdmin = this.auth.isAdmin();

        // 1. Sidebar menu visibility
        const menuTaiKhoan = document.querySelector('.sidebar-link[href*="tai-khoan.html"]');
        if (menuTaiKhoan && !isAdmin) {
            menuTaiKhoan.parentElement.style.display = 'none'; // Only ADMIN can access User Management
        }

        if (isStudent) {
            // Hide administrative menus for students
            const adminMenus = [
                '.sidebar-link[href*="toa-nha.html"]',
                '.sidebar-link[href*="loai-phong.html"]',
                '.sidebar-link[href*="sinh-vien.html"]',
                '.sidebar-link[href*="sinh-vien-phong.html"]'
            ];
            adminMenus.forEach(selector => {
                const el = document.querySelector(selector);
                if (el && el.parentElement) el.parentElement.style.display = 'none';
            });

            // Hide admin action buttons across pages
            document.querySelectorAll('.admin-only, [data-role="admin"], [data-role="staff"]').forEach(el => {
                el.style.display = 'none';
            });

            // In invoice page, hide "Lập hóa đơn hàng loạt" button
            const btnLapHangLoat = document.getElementById('btn-lap-hang-loat');
            if (btnLapHangLoat) btnLapHangLoat.style.display = 'none';
            const btnThemHoaDon = document.getElementById('btn-them-hoa-don');
            if (btnThemHoaDon) btnThemHoaDon.style.display = 'none';
            const navBaoCao = document.getElementById('tab-bao-cao-link');
            if (navBaoCao) navBaoCao.style.display = 'none';
        }
    }
};

window.App = App;
