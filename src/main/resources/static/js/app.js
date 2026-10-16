/**
 * Hệ Thống Quản Lý Ký Túc Xá Sinh Viên
 * app.js - Unified Client JavaScript Library
 */

const App = {
    // Current user state
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
        logout() {
            localStorage.removeItem('ktx_user');
            window.location.href = '/login.html';
        },
        requireAuth() {
            const user = this.getUser();
            if (!user && !window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('/login')) {
                // If not logged in, we set a default demo session for evaluation if needed or redirect
                // Let's redirect to login.html
                window.location.href = '/login.html';
            }
            return user;
        }
    },

    // HTTP API Request wrapper with error handling
    async api(url, options = {}) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...options.headers
            },
            ...options
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
                let errorMsg = 'Có lỗi xảy ra khi xử lý yêu cầu';
                if (data && typeof data === 'object') {
                    errorMsg = data.detail || data.message || data.title || JSON.stringify(data);
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
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title fs-6 fw-bold" id="confirm-modal-title">Xác nhận</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body py-4" id="confirm-modal-body">
                                Nội dung xác nhận
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-sm btn-light border" data-bs-dismiss="modal">Hủy bỏ</button>
                                <button type="button" class="btn btn-sm" id="confirm-modal-btn">Đồng ý</button>
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
            confirmBtn.className = `btn btn-sm ${btnClass}`;
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

    // Initialize layout, topbar, active sidebar menu
    initLayout(activeMenu) {
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

        // Display current user
        const user = this.auth.getUser();
        const userNameEl = document.getElementById('topbar-user-name');
        const userRoleEl = document.getElementById('topbar-user-role');
        const userAvatarEl = document.getElementById('topbar-user-avatar');

        if (userNameEl) {
            userNameEl.textContent = user ? user.hoTen : 'Quản trị viên';
        }
        if (userRoleEl) {
            userRoleEl.textContent = user ? (user.maNguoiDung || 'Admin') : 'Admin KTX';
        }
        if (userAvatarEl) {
            const initial = user && user.hoTen ? user.hoTen.trim().charAt(0).toUpperCase() : 'A';
            userAvatarEl.textContent = initial;
        }
    }
};

window.App = App;
