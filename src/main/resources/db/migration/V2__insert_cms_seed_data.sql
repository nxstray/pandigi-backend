-- ============ LANDING PAGE ============

-- Hero Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'hero_title', 'TEXT', 'Menyediakan Solusi Digital yang mendukung pertumbuhan dan Transformasi Bisnis', 'Landing - Hero Title', 1, true),
('LANDING', 'hero_subtitle', 'TEXT', 'Solusi perangkat lunak, perangkat keras, dan pendampingan IT yang dirancang sesuai kebutuhan perusahaan.', 'Landing - Hero Subtitle', 2, true),
('LANDING', 'hero_cta_text', 'TEXT', 'Build with us', 'Landing - Hero CTA Button', 3, true),
('LANDING', 'hero_building_image', 'IMAGE_URL', 'building.png', 'Landing - Hero Building Image', 4, true);

-- What We Do Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'what_we_do_title', 'TEXT', 'What we do', 'Landing - What We Do Title', 10, true);

-- Software Service
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'service_software_title', 'TEXT', 'Software', 'Landing - Service Software Title', 20, true),
('LANDING', 'service_software_description', 'TEXT', 'Kami menyediakan solusi pengembangan perangkat lunak yang komprehensif, termasuk aplikasi web, aplikasi mobile, dan sistem enterprise yang disesuaikan dengan kebutuhan bisnis Anda.', 'Landing - Service Software Description', 21, true),
('LANDING', 'service_software_icon', 'IMAGE_URL', 'software-wwd.png', 'Landing - Service Software Icon', 22, true);

-- Hardware Service
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'service_hardware_title', 'TEXT', 'Hardware', 'Landing - Service Hardware Title', 30, true),
('LANDING', 'service_hardware_description', 'TEXT', 'Dari workstation hingga server, kami menyediakan dan memelihara infrastruktur perangkat keras berkualitas untuk mendukung operasi bisnis Anda secara efisien dan andal.', 'Landing - Service Hardware Description', 31, true),
('LANDING', 'service_hardware_icon', 'IMAGE_URL', 'hardware-wwd.png', 'Landing - Service Hardware Icon', 32, true);

-- Multimedia Service
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'service_multimedia_title', 'TEXT', 'Multimedia', 'Landing - Service Multimedia Title', 40, true),
('LANDING', 'service_multimedia_description', 'TEXT', 'Solusi multimedia kreatif termasuk produksi video, desain grafis, animasi, dan media interaktif untuk meningkatkan kehadiran brand Anda di pasar.', 'Landing - Service Multimedia Description', 41, true),
('LANDING', 'service_multimedia_icon', 'IMAGE_URL', 'media-wwd.png', 'Landing - Service Multimedia Icon', 42, true);

-- Computer Service
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'service_computer_title', 'TEXT', 'Computer', 'Landing - Service Computer Title', 50, true),
('LANDING', 'service_computer_description', 'TEXT', 'Layanan pemeliharaan komputer profesional, perbaikan, dan dukungan IT untuk menjaga teknologi Anda tetap berjalan lancar dan meminimalkan downtime.', 'Landing - Service Computer Description', 51, true),
('LANDING', 'service_computer_icon', 'IMAGE_URL', 'computer-wwd.png', 'Landing - Service Computer Icon', 52, true);

-- Who We Are Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'who_we_are_title', 'TEXT', 'Who we are', 'Landing - Who We Are Title', 60, true),
('LANDING', 'who_we_are_description', 'TEXT', 'PT. Pandawa Digital Mandiri adalah penyedia solusi teknologi terkemuka yang berkomitmen untuk memberikan layanan inovatif dan andal. Kami adalah tim profesional yang berdedikasi untuk memberikan keunggulan dalam solusi teknologi, mendukung transformasi digital bisnis Anda dengan pendekatan yang disesuaikan dan hasil yang terukur.', 'Landing - Who We Are Description', 61, true),
('LANDING', 'who_we_are_image_1', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Who We Are Image 1', 62, true),
('LANDING', 'who_we_are_image_2', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Who We Are Image 2', 63, true),
('LANDING', 'who_we_are_image_3', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Who We Are Image 3', 64, true);

-- Our Work Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'our_work_title', 'TEXT', 'Our work', 'Landing - Our Work Title', 70, true);

-- Portfolio Items
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'portfolio_1_title', 'TEXT', 'E-Commerce Platform', 'Landing - Portfolio 1 Title', 71, true),
('LANDING', 'portfolio_1_image', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Portfolio 1 Image', 72, true),
('LANDING', 'portfolio_2_title', 'TEXT', 'Corporate Website', 'Landing - Portfolio 2 Title', 73, true),
('LANDING', 'portfolio_2_image', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Portfolio 2 Image', 74, true),
('LANDING', 'portfolio_3_title', 'TEXT', 'Mobile Application', 'Landing - Portfolio 3 Title', 75, true),
('LANDING', 'portfolio_3_image', 'IMAGE_URL', 'dummy-photo.png', 'Landing - Portfolio 3 Image', 76, true);

-- Contact Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES
('LANDING', 'contact_title', 'TEXT', 'Get in touch with us', 'Landing - Contact Title', 100, true),
('LANDING', 'contact_phone_title', 'TEXT', 'Phone', 'Landing - Contact Phone Title', 101, true),
('LANDING', 'contact_phone_description', 'TEXT', '+62 21 1234 5678 - Tersedia Senin hingga Jumat, 9 AM - 5 PM', 'Landing - Contact Phone Description', 102, true),
('LANDING', 'contact_email_title', 'TEXT', 'Email', 'Landing - Contact Email Title', 103, true),
('LANDING', 'contact_email_description', 'TEXT', 'info@pandawadigital.com - Kami akan merespons dalam 24 jam', 'Landing - Contact Email Description', 104, true),
('LANDING', 'contact_social_title', 'TEXT', 'Social', 'Landing - Contact Social Title', 105, true),
('LANDING', 'contact_social_link_1', 'TEXT', 'https://facebook.com/pandawadigital', 'Landing - Contact Social Link Facebook', 106, true),
('LANDING', 'contact_social_link_2', 'TEXT', 'https://instagram.com/pandawadigital', 'Landing - Contact Social Link Instagram', 107, true),
('LANDING', 'contact_social_link_3', 'TEXT', 'https://linkedin.com/company/pandawadigital', 'Landing - Contact Social Link LinkedIn', 108, true),
('LANDING', 'contact_logo_image', 'IMAGE_URL', 'logo-text.png', 'Landing - Contact Logo Image', 109, true);

-- ============ WHAT WE DO PAGE ============

-- Hero Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHAT_WE_DO', 'hero_title', 'TEXT', 'What We Do', 'Hero Title', 1, true),
('WHAT_WE_DO', 'hero_subtitle', 'TEXT', 'Discover our services and how we can help you achieve your goals.', 'Hero Subtitle', 2, true),
('WHAT_WE_DO', 'hero_vector', 'IMAGE_URL', 'vector_logo_pandigi.png', 'Hero Vector Logo', 3, true),
('WHAT_WE_DO', 'hero_building_image', 'IMAGE_URL', 'building.png', 'Hero Building Image', 4, true);

-- What We Offer Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHAT_WE_DO', 'what_we_offer_title', 'TEXT', 'What we offer?', 'Services Section Title', 10, true);

-- Services Overview
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
-- Software Service
('WHAT_WE_DO', 'service_software_title', 'TEXT', 'Software', 'Software Service Title', 11, true),
('WHAT_WE_DO', 'service_software_code', 'TEXT', '(46152)', 'Software Service Code', 12, true),
('WHAT_WE_DO', 'service_software_description', 'TEXT', 'Kami menyediakan solusi pengembangan perangkat lunak yang komprehensif, mulai dari aplikasi web, mobile, hingga sistem enterprise yang disesuaikan dengan kebutuhan bisnis Anda.', 'Software Service Description', 13, true),
('WHAT_WE_DO', 'service_software_icon', 'IMAGE_URL', 'software-wwd.png', 'Software Service Icon', 14, true),

-- Hardware Service
('WHAT_WE_DO', 'service_hardware_title', 'TEXT', 'Hardware', 'Hardware Service Title', 15, true),
('WHAT_WE_DO', 'service_hardware_code', 'TEXT', '(46599)', 'Hardware Service Code', 16, true),
('WHAT_WE_DO', 'service_hardware_description', 'TEXT', 'Dari workstation hingga server enterprise, kami menyediakan infrastruktur hardware berkualitas tinggi yang mendukung operasional bisnis Anda dengan performa optimal.', 'Hardware Service Description', 17, true),
('WHAT_WE_DO', 'service_hardware_icon', 'IMAGE_URL', 'hardware-wwd.png', 'Hardware Service Icon', 18, true),

-- Multimedia Service
('WHAT_WE_DO', 'service_multimedia_title', 'TEXT', 'Multimedia', 'Multimedia Service Title', 19, true),
('WHAT_WE_DO', 'service_multimedia_code', 'TEXT', '(61929)', 'Multimedia Service Code', 20, true),
('WHAT_WE_DO', 'service_multimedia_description', 'TEXT', 'Solusi multimedia kreatif untuk meningkatkan kehadiran brand Anda, termasuk desain grafis, video production, animasi, dan konten digital yang engaging.', 'Multimedia Service Description', 21, true),
('WHAT_WE_DO', 'service_multimedia_icon', 'IMAGE_URL', 'media-wwd.png', 'Multimedia Service Icon', 22, true),

-- Computer Service
('WHAT_WE_DO', 'service_computer_title', 'TEXT', 'Computer', 'Computer Service Title', 23, true),
('WHAT_WE_DO', 'service_computer_code', 'TEXT', '(46511)', 'Computer Service Code', 24, true),
('WHAT_WE_DO', 'service_computer_description', 'TEXT', 'Layanan pemeliharaan komputer profesional dan dukungan IT yang responsif, memastikan sistem Anda selalu berjalan lancar tanpa hambatan.', 'Computer Service Description', 25, true),
('WHAT_WE_DO', 'service_computer_icon', 'IMAGE_URL', 'computer-wwd.png', 'Computer Service Icon', 26, true);

-- Service Detail Sections
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
-- Software Detail
('WHAT_WE_DO', 'detail_software_title', 'TEXT', 'Software', 'Software Detail Title', 31, true),
('WHAT_WE_DO', 'detail_software_description', 'TEXT', 'Tim developer kami yang berpengalaman siap membangun solusi software custom sesuai kebutuhan spesifik bisnis Anda. Dari aplikasi web modern, mobile apps iOS/Android, hingga sistem enterprise yang kompleks - kami menggunakan teknologi terkini dan best practices untuk memastikan hasil yang berkualitas tinggi, scalable, dan mudah dimaintain.', 'Software Detail Description', 32, true),
('WHAT_WE_DO', 'detail_software_image', 'IMAGE_URL', 'dummy-photo.png', 'Software Detail Image', 33, true),

-- Hardware Detail
('WHAT_WE_DO', 'detail_hardware_title', 'TEXT', 'Hardware', 'Hardware Detail Title', 34, true),
('WHAT_WE_DO', 'detail_hardware_description', 'TEXT', 'Kami menyediakan solusi hardware enterprise terlengkap, mulai dari workstation untuk kebutuhan profesional, server rack untuk data center, networking equipment, hingga storage solutions. Semua produk yang kami tawarkan adalah brand ternama dengan garansi resmi dan didukung oleh layanan purna jual yang excellent.', 'Hardware Detail Description', 35, true),
('WHAT_WE_DO', 'detail_hardware_image', 'IMAGE_URL', 'dummy-photo.png', 'Hardware Detail Image', 36, true),

-- Multimedia Detail
('WHAT_WE_DO', 'detail_multimedia_title', 'TEXT', 'Multimedia', 'Multimedia Detail Title', 37, true),
('WHAT_WE_DO', 'detail_multimedia_description', 'TEXT', 'Tingkatkan brand presence Anda dengan konten multimedia berkualitas. Tim kreatif kami menyediakan layanan desain grafis profesional, video production cinematik, animasi 2D/3D, motion graphics, hingga content creation untuk social media. Kami membantu mewujudkan visi kreatif Anda menjadi konten visual yang memukau dan efektif.', 'Multimedia Detail Description', 38, true),
('WHAT_WE_DO', 'detail_multimedia_image', 'IMAGE_URL', 'dummy-photo.png', 'Multimedia Detail Image', 39, true),

-- Computer Detail
('WHAT_WE_DO', 'detail_computer_title', 'TEXT', 'Computer', 'Computer Detail Title', 40, true),
('WHAT_WE_DO', 'detail_computer_description', 'TEXT', 'Jangan biarkan masalah teknis menghambat produktivitas bisnis Anda. Layanan maintenance dan support IT kami mencakup troubleshooting, preventive maintenance, sistem upgrade, data recovery, dan technical support 24/7. Tim teknisi bersertifikat kami siap membantu memastikan infrastruktur IT Anda selalu dalam kondisi optimal.', 'Computer Detail Description', 41, true),
('WHAT_WE_DO', 'detail_computer_image', 'IMAGE_URL', 'dummy-photo.png', 'Computer Detail Image', 42, true);

-- Contact Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHAT_WE_DO', 'contact_title', 'TEXT', 'Get in touch with us', 'Contact Section Title', 90, true),
('WHAT_WE_DO', 'contact_phone_title', 'TEXT', 'Phone', 'Contact Phone Label', 91, true),
('WHAT_WE_DO', 'contact_phone_description', 'TEXT', '+62 21 1234 5678 - Tersedia Senin hingga Jumat, 09:00 - 17:00 WIB', 'Contact Phone Description', 92, true),
('WHAT_WE_DO', 'contact_email_title', 'TEXT', 'Email', 'Contact Email Label', 93, true),
('WHAT_WE_DO', 'contact_email_description', 'TEXT', 'info@pandawadigital.com - Kami akan merespons dalam 24 jam', 'Contact Email Description', 94, true),
('WHAT_WE_DO', 'contact_social_title', 'TEXT', 'Social', 'Contact Social Label', 95, true),
('WHAT_WE_DO', 'contact_social_link_1', 'TEXT', '#', 'Social Link 1 (Facebook)', 96, true),
('WHAT_WE_DO', 'contact_social_link_2', 'TEXT', '#', 'Social Link 2 (Instagram)', 97, true),
('WHAT_WE_DO', 'contact_social_link_3', 'TEXT', '#', 'Social Link 3 (LinkedIn)', 98, true),
('WHAT_WE_DO', 'contact_logo_image', 'IMAGE_URL', 'logo-text.png', 'Contact Logo Image', 99, true);

-- ============ WHO WE ARE PAGE ============

-- Hero Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'hero_title', 'TEXT', 'Who We Are', 'Hero Title', 1, true),
('WHO_WE_ARE', 'hero_subtitle', 'TEXT', 'Discover Pandigi''s Journey and Values', 'Hero Subtitle', 2, true),
('WHO_WE_ARE', 'hero_vector', 'IMAGE_URL', 'vector_logo_pandigi.png', 'Hero Vector Logo', 3, true),
('WHO_WE_ARE', 'hero_building_image', 'IMAGE_URL', 'building.png', 'Hero Building Image', 4, true);

-- Timeline Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'timeline_title', 'TEXT', 'Our Journey', 'Timeline Section Title', 10, true),

('WHO_WE_ARE', 'timeline_1_date', 'TEXT', '2010', 'Timeline 1 Date', 11, true),
('WHO_WE_ARE', 'timeline_1_title', 'TEXT', 'Berdirinya Perusahaan', 'Timeline 1 Title', 12, true),
('WHO_WE_ARE', 'timeline_1_description', 'TEXT', 'PT. Pandawa Digital Mandiri didirikan dengan visi menyediakan solusi teknologi terbaik untuk bisnis di Indonesia.', 'Timeline 1 Description', 13, true),

('WHO_WE_ARE', 'timeline_2_date', 'TEXT', '2013', 'Timeline 2 Date', 14, true),
('WHO_WE_ARE', 'timeline_2_title', 'TEXT', 'Ekspansi Layanan', 'Timeline 2 Title', 15, true),
('WHO_WE_ARE', 'timeline_2_description', 'TEXT', 'Memperluas portofolio layanan dengan menambahkan solusi multimedia dan hardware enterprise.', 'Timeline 2 Description', 16, true),

('WHO_WE_ARE', 'timeline_3_date', 'TEXT', '2016', 'Timeline 3 Date', 17, true),
('WHO_WE_ARE', 'timeline_3_title', 'TEXT', '100+ Klien', 'Timeline 3 Title', 18, true),
('WHO_WE_ARE', 'timeline_3_description', 'TEXT', 'Mencapai milestone 100 klien korporat yang mempercayakan solusi digital mereka kepada kami.', 'Timeline 3 Description', 19, true),

('WHO_WE_ARE', 'timeline_4_date', 'TEXT', '2019', 'Timeline 4 Date', 20, true),
('WHO_WE_ARE', 'timeline_4_title', 'TEXT', 'Sertifikasi Internasional', 'Timeline 4 Title', 21, true),
('WHO_WE_ARE', 'timeline_4_description', 'TEXT', 'Memperoleh sertifikasi ISO 9001:2015 untuk standar kualitas manajemen internasional.', 'Timeline 4 Description', 22, true),

('WHO_WE_ARE', 'timeline_5_date', 'TEXT', '2022', 'Timeline 5 Date', 23, true),
('WHO_WE_ARE', 'timeline_5_title', 'TEXT', 'Inovasi AI & Cloud', 'Timeline 5 Title', 24, true),
('WHO_WE_ARE', 'timeline_5_description', 'TEXT', 'Meluncurkan divisi khusus untuk solusi Artificial Intelligence dan Cloud Computing.', 'Timeline 5 Description', 25, true),

('WHO_WE_ARE', 'timeline_6_date', 'TEXT', '2025', 'Timeline 6 Date', 26, true),
('WHO_WE_ARE', 'timeline_6_title', 'TEXT', 'Digital Transformation Leader', 'Timeline 6 Title', 27, true),
('WHO_WE_ARE', 'timeline_6_description', 'TEXT', 'Menjadi salah satu pemimpin transformasi digital di Indonesia dengan 500+ proyek sukses.', 'Timeline 6 Description', 28, true);

-- Vision & Mission Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'vision_text', 'TEXT', 'Menjadi mitra terpercaya dalam transformasi digital, menghadirkan solusi teknologi inovatif yang memberdayakan bisnis untuk tumbuh dan berkembang di era digital.', 'Vision Text', 30, true),

('WHO_WE_ARE', 'mission_1', 'TEXT', 'Menyediakan solusi teknologi berkualitas tinggi yang disesuaikan dengan kebutuhan spesifik setiap klien.', 'Mission 1', 31, true),
('WHO_WE_ARE', 'mission_2', 'TEXT', 'Membangun hubungan jangka panjang dengan klien melalui layanan profesional dan dukungan berkelanjutan.', 'Mission 2', 32, true),
('WHO_WE_ARE', 'mission_3', 'TEXT', 'Terus berinovasi dan mengadopsi teknologi terkini untuk memberikan solusi terdepan.', 'Mission 3', 33, true),
('WHO_WE_ARE', 'mission_4', 'TEXT', 'Mengembangkan tim profesional yang kompeten dan berkomitmen pada keunggulan.', 'Mission 4', 34, true);

-- Services Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'services_title', 'TEXT', 'Layanan Kami', 'Services Section Title', 40, true),

('WHO_WE_ARE', 'service_software_title', 'TEXT', 'Software Development', 'Software Service Title', 41, true),
('WHO_WE_ARE', 'service_software_description', 'TEXT', 'Solusi pengembangan perangkat lunak custom yang disesuaikan dengan kebutuhan bisnis Anda, dari web apps hingga enterprise systems.', 'Software Service Description', 42, true),
('WHO_WE_ARE', 'service_software_image', 'IMAGE_URL', 'dummy-photo.png', 'Software Service Image', 43, true),

('WHO_WE_ARE', 'service_hardware_title', 'TEXT', 'Hardware Solutions', 'Hardware Service Title', 44, true),
('WHO_WE_ARE', 'service_hardware_description', 'TEXT', 'Penyedia infrastruktur hardware enterprise terlengkap, dari workstation profesional hingga server data center.', 'Hardware Service Description', 45, true),
('WHO_WE_ARE', 'service_hardware_image', 'IMAGE_URL', 'dummy-photo.png', 'Hardware Service Image', 46, true),

('WHO_WE_ARE', 'service_multimedia_title', 'TEXT', 'Multimedia Creative', 'Multimedia Service Title', 47, true),
('WHO_WE_ARE', 'service_multimedia_description', 'TEXT', 'Layanan kreatif multimedia untuk meningkatkan brand presence, dari desain grafis hingga video production.', 'Multimedia Service Description', 48, true),
('WHO_WE_ARE', 'service_multimedia_image', 'IMAGE_URL', 'dummy-photo.png', 'Multimedia Service Image', 49, true),

('WHO_WE_ARE', 'service_computer_title', 'TEXT', 'IT Support & Maintenance', 'Computer Service Title', 50, true),
('WHO_WE_ARE', 'service_computer_description', 'TEXT', 'Dukungan IT profesional 24/7 dan pemeliharaan sistem untuk memastikan operasional bisnis berjalan lancar.', 'Computer Service Description', 51, true),
('WHO_WE_ARE', 'service_computer_image', 'IMAGE_URL', 'dummy-photo.png', 'Computer Service Image', 52, true);

-- Team Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'team_title', 'TEXT', 'Tim Kami', 'Team Section Title', 60, true),

('WHO_WE_ARE', 'team_1_name', 'TEXT', 'Ahmad Yusuf', 'Team Member 1 Name', 61, true),
('WHO_WE_ARE', 'team_1_description', 'TEXT', 'CEO & Founder - Memimpin visi strategis perusahaan dengan pengalaman 15+ tahun di industri teknologi.', 'Team Member 1 Description', 62, true),
('WHO_WE_ARE', 'team_1_image', 'IMAGE_URL', 'dummy-photo.png', 'Team Member 1 Image', 63, true),

('WHO_WE_ARE', 'team_2_name', 'TEXT', 'Siti Rahma', 'Team Member 2 Name', 64, true),
('WHO_WE_ARE', 'team_2_description', 'TEXT', 'CTO - Mengelola arsitektur teknologi dan inovasi produk dengan keahlian cloud computing dan AI.', 'Team Member 2 Description', 65, true),
('WHO_WE_ARE', 'team_2_image', 'IMAGE_URL', 'dummy-photo.png', 'Team Member 2 Image', 66, true),

('WHO_WE_ARE', 'team_3_name', 'TEXT', 'Budi Santoso', 'Team Member 3 Name', 67, true),
('WHO_WE_ARE', 'team_3_description', 'TEXT', 'Head of Development - Memimpin tim developer dalam menghasilkan solusi software berkualitas tinggi.', 'Team Member 3 Description', 68, true),
('WHO_WE_ARE', 'team_3_image', 'IMAGE_URL', 'dummy-photo.png', 'Team Member 3 Image', 69, true),

('WHO_WE_ARE', 'team_4_name', 'TEXT', 'Diana Putri', 'Team Member 4 Name', 70, true),
('WHO_WE_ARE', 'team_4_description', 'TEXT', 'Creative Director - Mengarahkan visi kreatif untuk semua proyek multimedia dan branding.', 'Team Member 4 Description', 71, true),
('WHO_WE_ARE', 'team_4_image', 'IMAGE_URL', 'dummy-photo.png', 'Team Member 4 Image', 72, true);

-- Reviews Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'reviews_title', 'TEXT', 'Ulasan Client', 'Reviews Section Title', 80, true),

('WHO_WE_ARE', 'review_1_description', 'TEXT', 'Pandigi memberikan solusi yang sangat profesional dan tepat waktu. Tim mereka sangat responsif dan memahami kebutuhan bisnis kami dengan baik.', 'Review 1 Description', 81, true),
('WHO_WE_ARE', 'review_1_image', 'IMAGE_URL', 'dummy-photo.png', 'Review 1 Image', 82, true),

('WHO_WE_ARE', 'review_2_description', 'TEXT', 'Kualitas pekerjaan yang luar biasa! Sistem yang dikembangkan Pandigi meningkatkan efisiensi operasional kami hingga 40%. Sangat merekomendasikan!', 'Review 2 Description', 83, true),
('WHO_WE_ARE', 'review_2_image', 'IMAGE_URL', 'dummy-photo.png', 'Review 2 Image', 84, true),

('WHO_WE_ARE', 'review_3_description', 'TEXT', 'Partner teknologi terbaik yang pernah kami ajak bekerjasama. Dukungan after-sales mereka sangat membantu dalam maintenance sistem kami.', 'Review 3 Description', 85, true),
('WHO_WE_ARE', 'review_3_image', 'IMAGE_URL', 'dummy-photo.png', 'Review 3 Image', 86, true),

('WHO_WE_ARE', 'review_4_description', 'TEXT', 'Dari konsultasi hingga implementasi, Pandigi menunjukkan profesionalisme tinggi. Hasilnya melebihi ekspektasi kami!', 'Review 4 Description', 87, true),
('WHO_WE_ARE', 'review_4_image', 'IMAGE_URL', 'dummy-photo.png', 'Review 4 Image', 88, true);

-- Contact Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('WHO_WE_ARE', 'contact_title', 'TEXT', 'Get in touch with us', 'Contact Section Title', 90, true),
('WHO_WE_ARE', 'contact_phone_title', 'TEXT', 'Phone', 'Contact Phone Label', 91, true),
('WHO_WE_ARE', 'contact_phone_description', 'TEXT', '+62 21 1234 5678 - Tersedia Senin hingga Jumat, 09:00 - 17:00 WIB', 'Contact Phone Description', 92, true),
('WHO_WE_ARE', 'contact_email_title', 'TEXT', 'Email', 'Contact Email Label', 93, true),
('WHO_WE_ARE', 'contact_email_description', 'TEXT', 'info@pandawadigital.com - Kami akan merespons dalam 24 jam', 'Contact Email Description', 94, true),
('WHO_WE_ARE', 'contact_social_title', 'TEXT', 'Social', 'Contact Social Label', 95, true),
('WHO_WE_ARE', 'contact_social_link_1', 'TEXT', '#', 'Social Link 1 (Facebook)', 96, true),
('WHO_WE_ARE', 'contact_social_link_2', 'TEXT', '#', 'Social Link 2 (Instagram)', 97, true),
('WHO_WE_ARE', 'contact_social_link_3', 'TEXT', '#', 'Social Link 3 (LinkedIn)', 98, true),
('WHO_WE_ARE', 'contact_logo_image', 'IMAGE_URL', 'logo-text.png', 'Contact Logo Image', 99, true);

-- ============ OUR WORK PAGE ============

-- Hero Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('OUR_WORK', 'hero_title', 'TEXT', 'Our Work', 'Hero Title', 1, true),
('OUR_WORK', 'hero_subtitle', 'TEXT', 'Discover Our Success Stories, Innovations, and Client Partnerships', 'Hero Subtitle', 2, true),
('OUR_WORK', 'hero_vector', 'IMAGE_URL', 'vector_logo_pandigi.png', 'Hero Vector Logo', 3, true),
('OUR_WORK', 'hero_building_image', 'IMAGE_URL', 'building.png', 'Hero Building Image', 4, true);

-- Contact Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('OUR_WORK', 'contact_title', 'TEXT', 'Get in touch with us', 'Contact Section Title', 90, true),
('OUR_WORK', 'contact_phone_title', 'TEXT', 'Phone', 'Contact Phone Label', 91, true),
('OUR_WORK', 'contact_phone_description', 'TEXT', '+62 21 1234 5678 - Tersedia Senin hingga Jumat, 09:00 - 17:00 WIB', 'Contact Phone Description', 92, true),
('OUR_WORK', 'contact_email_title', 'TEXT', 'Email', 'Contact Email Label', 93, true),
('OUR_WORK', 'contact_email_description', 'TEXT', 'info@pandawadigital.com - Kami akan merespons dalam 24 jam', 'Contact Email Description', 94, true),
('OUR_WORK', 'contact_social_title', 'TEXT', 'Social', 'Contact Social Label', 95, true),
('OUR_WORK', 'contact_social_link_1', 'TEXT', '#', 'Social Link 1 (Facebook)', 96, true),
('OUR_WORK', 'contact_social_link_2', 'TEXT', '#', 'Social Link 2 (Instagram)', 97, true),
('OUR_WORK', 'contact_social_link_3', 'TEXT', '#', 'Social Link 3 (LinkedIn)', 98, true),
('OUR_WORK', 'contact_logo_image', 'IMAGE_URL', 'logo-text.png', 'Contact Logo Image', 99, true);

-- ============ BUILD WITH US PAGE ============

-- Hero Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('BUILD_WITH_US', 'hero_title', 'TEXT', 'Request a Consultation Form', 'Hero Title', 1, true),
('BUILD_WITH_US', 'hero_subtitle', 'TEXT', 'Let''s Build Something Great Together', 'Hero Subtitle', 2, true),
('BUILD_WITH_US', 'hero_vector', 'IMAGE_URL', 'vector_logo_pandigi.png', 'Hero Vector Logo', 3, true),
('BUILD_WITH_US', 'hero_building_image', 'IMAGE_URL', 'building.png', 'Hero Building Image', 4, true);

-- Form Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('BUILD_WITH_US', 'form_section_title', 'TEXT', 'Lets get started', 'Form Section Title', 10, true),
('BUILD_WITH_US', 'form_section_subtitle', 'TEXT', 'Build With Us Here', 'Form Section Subtitle', 11, true),
('BUILD_WITH_US', 'form_section_description', 'TEXT', 'Ceritakan kebutuhan project Anda dan tim kami akan segera menghubungi untuk memberikan solusi terbaik.', 'Form Section Description', 12, true);

-- Footer Section
INSERT INTO content_page (page_name, section_key, content_type, content_value, content_label, display_order, is_active) VALUES 
('BUILD_WITH_US', 'footer_logo', 'IMAGE_URL', 'logo-no-bg.png', 'Footer Logo', 90, true),
('BUILD_WITH_US', 'footer_text', 'TEXT', 'PT. Pandawa Digital Mandiri - Mitra terpercaya untuk transformasi digital bisnis Anda sejak 2010.', 'Footer Text', 91, true),
('BUILD_WITH_US', 'footer_social_link_1', 'TEXT', '#', 'Footer Social Link 1', 92, true),
('BUILD_WITH_US', 'footer_social_link_2', 'TEXT', '#', 'Footer Social Link 2', 93, true);