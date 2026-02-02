-- ============ ADMIN TABLE ============
CREATE TABLE admin (
    id_admin SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('SUPER_ADMIN', 'MANAGER')),
    is_active BOOLEAN DEFAULT TRUE,
    is_first_login BOOLEAN DEFAULT TRUE,
    foto_profil TEXT,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_username ON admin(username);
CREATE INDEX idx_admin_email ON admin(email);

-- ============ MANAGER TABLE ============
CREATE TABLE manager (
    id_manager SERIAL PRIMARY KEY,
    nama_manager VARCHAR(100) NOT NULL,
    email_manager VARCHAR(100) UNIQUE NOT NULL,
    no_telp VARCHAR(20) NOT NULL,
    divisi VARCHAR(50) NOT NULL,
    tgl_mulai DATE
);

CREATE INDEX idx_manager_email ON manager(email_manager);
CREATE INDEX idx_manager_divisi ON manager(divisi);

-- ============ KARYAWAN TABLE ============
CREATE TABLE karyawan (
    id_karyawan SERIAL PRIMARY KEY,
    nama_karyawan VARCHAR(100) NOT NULL,
    email_karyawan VARCHAR(100) UNIQUE NOT NULL,
    no_telp VARCHAR(20) NOT NULL,
    jabatan_posisi VARCHAR(50) NOT NULL,
    foto_profil TEXT,
    id_manager INTEGER NOT NULL,
    CONSTRAINT fk_karyawan_manager FOREIGN KEY (id_manager) REFERENCES manager(id_manager) ON DELETE CASCADE
);

CREATE INDEX idx_karyawan_email ON karyawan(email_karyawan);
CREATE INDEX idx_karyawan_manager ON karyawan(id_manager);

-- ============ KLIEN TABLE ============
CREATE TABLE klien (
    id_klien SERIAL PRIMARY KEY,
    nama_klien VARCHAR(100) NOT NULL,
    email_klien VARCHAR(100) UNIQUE NOT NULL,
    no_telp VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('BELUM', 'AKTIF')),
    tgl_request DATE,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX idx_klien_email ON klien(email_klien);
CREATE INDEX idx_klien_status ON klien(status);
CREATE INDEX idx_klien_deleted ON klien(is_deleted);

-- ============ MANAGER_KLIEN TABLE (Many-to-Many) ============
CREATE TABLE manager_klien (
    id_manager INTEGER NOT NULL,
    id_klien INTEGER NOT NULL,
    PRIMARY KEY (id_manager, id_klien),
    CONSTRAINT fk_manager_klien_manager FOREIGN KEY (id_manager) REFERENCES manager(id_manager) ON DELETE CASCADE,
    CONSTRAINT fk_manager_klien_klien FOREIGN KEY (id_klien) REFERENCES klien(id_klien) ON DELETE CASCADE
);

CREATE INDEX idx_manager_klien_manager ON manager_klien(id_manager);
CREATE INDEX idx_manager_klien_klien ON manager_klien(id_klien);

-- ============ CONTENT_PAGE TABLE ============
CREATE TABLE content_page (
    id_content SERIAL PRIMARY KEY,
    page_name VARCHAR(50) NOT NULL CHECK (page_name IN ('LANDING', 'WHAT_WE_DO', 'WHO_WE_ARE', 'OUR_WORK', 'BUILD_WITH_US')),
    section_key VARCHAR(100) NOT NULL,
    content_type VARCHAR(20) NOT NULL CHECK (content_type IN ('TEXT', 'HTML', 'IMAGE_URL', 'JSON', 'NUMBER')),
    content_value TEXT,
    content_label VARCHAR(200),
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    updated_by_admin_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_admin FOREIGN KEY (updated_by_admin_id) REFERENCES admin(id_admin) ON DELETE SET NULL
);

CREATE INDEX idx_page_section ON content_page(page_name, section_key);
CREATE INDEX idx_active ON content_page(is_active);
CREATE INDEX idx_content_order ON content_page(page_name, display_order);

-- ============ UPLOAD_FILE TABLE ============
CREATE TABLE upload_file (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255),
    original_name VARCHAR(255),
    content_type VARCHAR(100),
    size BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_upload_filename ON upload_file(filename);

-- ============ LAYANAN TABLE ============
CREATE TABLE layanan (
    id_layanan SERIAL PRIMARY KEY,
    nama_layanan VARCHAR(100) NOT NULL,
    kategori VARCHAR(30) NOT NULL CHECK (kategori IN ('SOSIAL', 'PIRANTI_LUNAK', 'MULTIMEDIA', 'MESIN_SEKURITAS')),
    catatan TEXT
);

CREATE INDEX idx_layanan_kategori ON layanan(kategori);

-- ============ REQUEST_LAYANAN TABLE ============
CREATE TABLE request_layanan (
    id_request SERIAL PRIMARY KEY,
    id_layanan INTEGER NOT NULL,
    id_klien INTEGER NOT NULL,
    approved_by_manager_id INTEGER,
    approved_by_name VARCHAR(100),
    tgl_request TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL CHECK (status IN ('MENUNGGU_VERIFIKASI', 'VERIFIKASI', 'DITOLAK')),
    tgl_verifikasi DATE,
    keterangan_penolakan TEXT,
    perusahaan VARCHAR(200),
    topic VARCHAR(100),
    pesan TEXT,
    anggaran VARCHAR(50),
    waktu_implementasi VARCHAR(50),
    skor_prioritas VARCHAR(20),
    kategori_lead VARCHAR(50),
    alasan_skor TEXT,
    tgl_analisa_ai TIMESTAMP,
    ai_analyzed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_request_layanan FOREIGN KEY (id_layanan) REFERENCES layanan(id_layanan) ON DELETE CASCADE,
    CONSTRAINT fk_request_klien FOREIGN KEY (id_klien) REFERENCES klien(id_klien) ON DELETE CASCADE,
    CONSTRAINT fk_request_manager FOREIGN KEY (approved_by_manager_id) REFERENCES manager(id_manager) ON DELETE SET NULL
);

CREATE INDEX idx_request_status ON request_layanan(status);
CREATE INDEX idx_request_layanan ON request_layanan(id_layanan);
CREATE INDEX idx_request_klien ON request_layanan(id_klien);
CREATE INDEX idx_request_ai_analyzed ON request_layanan(ai_analyzed);
CREATE INDEX idx_request_tgl ON request_layanan(tgl_request);

-- ============ NOTIFICATIONS TABLE ============
CREATE TABLE notifications (
    id_notification SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    link VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_notification_read ON notifications(is_read);
CREATE INDEX idx_notification_created ON notifications(created_at);
CREATE INDEX idx_notification_type ON notifications(type);

-- ============ REKAP TABLE ============
CREATE TABLE rekap (
    id_meeting SERIAL PRIMARY KEY,
    id_klien INTEGER NOT NULL,
    id_manager INTEGER,
    id_layanan INTEGER NOT NULL,
    nama_manager_manual VARCHAR(100),
    tgl_meeting DATE,
    hasil TEXT,
    status VARCHAR(100) NOT NULL CHECK (status IN ('MASIH_JALAN', 'TIDAK_LAGI_PAKAI_PANJANG_KARAKTER_UNTUK_CATATAN')),
    catatan TEXT,
    CONSTRAINT fk_rekap_klien FOREIGN KEY (id_klien) REFERENCES klien(id_klien) ON DELETE CASCADE,
    CONSTRAINT fk_rekap_manager FOREIGN KEY (id_manager) REFERENCES manager(id_manager) ON DELETE SET NULL,
    CONSTRAINT fk_rekap_layanan FOREIGN KEY (id_layanan) REFERENCES layanan(id_layanan) ON DELETE CASCADE
);

CREATE INDEX idx_rekap_klien ON rekap(id_klien);
CREATE INDEX idx_rekap_manager ON rekap(id_manager);
CREATE INDEX idx_rekap_layanan ON rekap(id_layanan);
CREATE INDEX idx_rekap_status ON rekap(status);
CREATE INDEX idx_rekap_tgl ON rekap(tgl_meeting);

-- ============ PROJECTS TABLE ============
CREATE TABLE projects (
    id_project SERIAL PRIMARY KEY,
    project_title VARCHAR(200) NOT NULL,
    project_description TEXT,
    project_category VARCHAR(50) NOT NULL CHECK (project_category IN ('WEB_DEVELOPMENT', 'MOBILE_APP', 'UI_UX_DESIGN', 'DIGITAL_MARKETING', 'AI_ML', 'CLOUD_INFRASTRUCTURE', 'OTHER')),
    project_image TEXT,
    project_client VARCHAR(150),
    project_year INTEGER,
    project_technologies TEXT,
    project_url VARCHAR(500),
    is_featured BOOLEAN DEFAULT FALSE,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by_admin_id INTEGER,
    CONSTRAINT fk_project_admin FOREIGN KEY (updated_by_admin_id) REFERENCES admin(id_admin) ON DELETE SET NULL
);

CREATE INDEX idx_project_category ON projects(project_category);
CREATE INDEX idx_project_year ON projects(project_year);
CREATE INDEX idx_project_active ON projects(is_active);
CREATE INDEX idx_project_featured ON projects(is_featured);
CREATE INDEX idx_project_order ON projects(display_order);