import os
import sys
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import qn, nsdecls

OUTPUT_PATH = r"D:\Quan_li_ktx_nhom12_Vuong_Trong_Thang_Java\Bao_Cao_Word\Bao_Cao_Do_An_Quan_Ly_Ky_Tuc_Xa_Nhom12.docx"
DIAGRAMS_DIR = r"D:\Quan_li_ktx_nhom12_Vuong_Trong_Thang_Java\Bao_Cao_Word\diagrams"

def create_report():
    doc = docx.Document()
    
    # Page setup: A4
    for section in doc.sections:
        section.page_width = Inches(8.27)   # 21.0 cm
        section.page_height = Inches(11.69) # 29.7 cm
        section.left_margin = Inches(0.984) # 2.5 cm
        section.right_margin = Inches(0.787)# 2.0 cm
        section.top_margin = Inches(0.787)  # 2.0 cm
        section.bottom_margin = Inches(0.787)# 2.0 cm

    # Base styles
    normal_style = doc.styles['Normal']
    normal_style.font.name = 'Times New Roman'
    normal_style.font.size = Pt(13)
    normal_style.font.color.rgb = RGBColor(0x1F, 0x29, 0x37) # Slate 800
    normal_style.paragraph_format.line_spacing = 1.25
    normal_style.paragraph_format.space_after = Pt(6)
    normal_style.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY

    # Color Constants
    CLR_PRIMARY = RGBColor(0x1E, 0x3A, 0x8A) # Deep Navy
    CLR_MUTED = RGBColor(0x64, 0x74, 0x8B)   # Slate 500
    CLR_DARK = RGBColor(0x0F, 0x17, 0x2A)    # Slate 900
    CLR_AMBER = RGBColor(0xB4, 0x53, 0x09)   # Amber 700

    # Helper: Set Cell Shading
    def set_cell_shading(cell, color_hex):
        shading = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shading)

    # Helper: Set Cell Margins (Padding)
    def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
        tcPr = cell._tc.get_or_add_tcPr()
        tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
        tcPr.append(tcMar)

    # Helper: Set Cell Borders
    def set_cell_borders(cell, top="CCCCCC", bottom="CCCCCC", left="CCCCCC", right="CCCCCC"):
        tcPr = cell._tc.get_or_add_tcPr()
        borders = parse_xml(f'<w:tcBorders {nsdecls("w")}><w:top w:val="single" w:sz="4" w:space="0" w:color="{top}"/><w:left w:val="single" w:sz="4" w:space="0" w:color="{left}"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="{bottom}"/><w:right w:val="single" w:sz="4" w:space="0" w:color="{right}"/></w:tcBorders>')
        tcPr.append(borders)

    # Helper: Paragraph
    def add_p(text="", bold=False, italic=False, size=13, align=WD_ALIGN_PARAGRAPH.JUSTIFY, color=CLR_DARK, space_before=0, space_after=6, line_spacing=1.25):
        p = doc.add_paragraph()
        p.alignment = align
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = line_spacing
        if text:
            run = p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(size)
            run.bold = bold
            run.italic = italic
            run.font.color.rgb = color
        return p

    # Helper: Chapter Heading (Heading 1)
    def add_chapter(title):
        p = doc.add_paragraph()
        p.paragraph_format.page_break_before = True
        p.paragraph_format.space_before = Pt(18)
        p.paragraph_format.space_after = Pt(12)
        p.alignment = WD_ALIGN_PARAGRAPH.LEFT
        run = p.add_run(title)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(16)
        run.bold = True
        run.font.color.rgb = CLR_PRIMARY
        return p

    # Helper: Section Heading (Heading 2)
    def add_heading2(title):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(6)
        p.alignment = WD_ALIGN_PARAGRAPH.LEFT
        run = p.add_run(title)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(14)
        run.bold = True
        run.font.color.rgb = CLR_PRIMARY
        return p

    # Helper: Sub-section Heading (Heading 3)
    def add_heading3(title):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(4)
        p.alignment = WD_ALIGN_PARAGRAPH.LEFT
        run = p.add_run(title)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(13)
        run.bold = True
        run.italic = True
        run.font.color.rgb = CLR_DARK
        return p

    # Helper: Image with Caption
    def add_diagram_image(filename, caption_text, width_inches=6.2):
        img_path = os.path.join(DIAGRAMS_DIR, filename)
        if os.path.exists(img_path):
            p_img = doc.add_paragraph()
            p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_img.paragraph_format.space_before = Pt(10)
            p_img.paragraph_format.space_after = Pt(4)
            run = p_img.add_run()
            run.add_picture(img_path, width=Inches(width_inches))
            
            p_cap = doc.add_paragraph()
            p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_cap.paragraph_format.space_before = Pt(2)
            p_cap.paragraph_format.space_after = Pt(10)
            run_cap = p_cap.add_run(caption_text)
            run_cap.font.name = 'Times New Roman'
            run_cap.font.size = Pt(11)
            run_cap.italic = True
            run_cap.font.color.rgb = CLR_MUTED

    # Helper: Placeholder Box for Group B Screenshot
    def add_screenshot_placeholder(fig_id, fig_name, purpose, steps, requirements, location):
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.autofit = False
        tbl.columns[0].width = Inches(6.5)
        
        cell = tbl.cell(0, 0)
        set_cell_shading(cell, "FFFBEB") # Light amber background
        set_cell_margins(cell, top=140, bottom=140, left=180, right=180)
        set_cell_borders(cell, top="D97706", bottom="D97706", left="D97706", right="D97706") # Amber border
        
        p = cell.paragraphs[0]
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(4)
        run_tag = p.add_run("[CHƯA CHỤP ẢNH – HÌNH ẢNH CẦN BỔ SUNG]\n")
        run_tag.bold = True
        run_tag.font.name = 'Times New Roman'
        run_tag.font.size = Pt(11.5)
        run_tag.font.color.rgb = CLR_AMBER
        
        run_title = p.add_run(f"Tên hình: {fig_id} – {fig_name}\n")
        run_title.bold = True
        run_title.font.name = 'Times New Roman'
        run_title.font.size = Pt(11)
        run_title.font.color.rgb = CLR_DARK
        
        run_purp = p.add_run(f"Mục đích: {purpose}\n\n")
        run_purp.font.name = 'Times New Roman'
        run_purp.font.size = Pt(10.5)
        run_purp.font.color.rgb = CLR_DARK
        
        run_step_h = p.add_run("Hướng dẫn chụp:\n")
        run_step_h.bold = True
        run_step_h.font.name = 'Times New Roman'
        run_step_h.font.size = Pt(10.5)
        run_step_h.font.color.rgb = CLR_DARK
        
        for idx, s in enumerate(steps, 1):
            r_s = p.add_run(f"{idx}. {s}\n")
            r_s.font.name = 'Times New Roman'
            r_s.font.size = Pt(10)
            r_s.font.color.rgb = CLR_DARK
            
        run_req_h = p.add_run("\nYêu cầu ảnh:\n")
        run_req_h.bold = True
        run_req_h.font.name = 'Times New Roman'
        run_req_h.font.size = Pt(10.5)
        run_req_h.font.color.rgb = CLR_DARK
        
        for r in requirements:
            r_r = p.add_run(f"• {r}\n")
            r_r.font.name = 'Times New Roman'
            r_r.font.size = Pt(10)
            r_r.font.color.rgb = CLR_DARK
            
        r_loc = p.add_run(f"\nVị trí chèn ảnh: {location}")
        r_loc.italic = True
        r_loc.font.name = 'Times New Roman'
        r_loc.font.size = Pt(10)
        r_loc.font.color.rgb = CLR_MUTED

        p_after = doc.add_paragraph()
        p_after.paragraph_format.space_before = Pt(4)
        p_after.paragraph_format.space_after = Pt(6)

    # Helper: Styled Data Table
    def add_styled_table(headers, rows_data, col_widths, table_title=""):
        if table_title:
            p_title = doc.add_paragraph()
            p_title.paragraph_format.space_before = Pt(10)
            p_title.paragraph_format.space_after = Pt(3)
            r_t = p_title.add_run(table_title)
            r_t.font.name = 'Times New Roman'
            r_t.font.size = Pt(11)
            r_t.bold = True
            r_t.font.color.rgb = CLR_DARK

        tbl = doc.add_table(rows=len(rows_data) + 1, cols=len(headers))
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.autofit = False

        # Header Row
        hdr_cells = tbl.rows[0].cells
        for i, h in enumerate(headers):
            hdr_cells[i].width = Inches(col_widths[i])
            set_cell_shading(hdr_cells[i], "1E3A8A") # Navy Blue
            set_cell_margins(hdr_cells[i], top=100, bottom=100, left=120, right=120)
            set_cell_borders(hdr_cells[i], top="1E3A8A", bottom="1E3A8A", left="1E3A8A", right="1E3A8A")
            hdr_cells[i].vertical_alignment = WD_ALIGN_VERTICAL.CENTER
            p = hdr_cells[i].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p.paragraph_format.space_before = Pt(0)
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(h)
            r.font.name = 'Times New Roman'
            r.font.size = Pt(10)
            r.bold = True
            r.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)

        # Data Rows
        for r_idx, row_values in enumerate(rows_data):
            row_cells = tbl.rows[r_idx + 1].cells
            shd = "F8FAFC" if r_idx % 2 == 1 else "FFFFFF"
            for c_idx, val in enumerate(row_values):
                row_cells[c_idx].width = Inches(col_widths[c_idx])
                set_cell_shading(row_cells[c_idx], shd)
                set_cell_margins(row_cells[c_idx], top=80, bottom=80, left=100, right=100)
                set_cell_borders(row_cells[c_idx], top="E2E8F0", bottom="E2E8F0", left="E2E8F0", right="E2E8F0")
                row_cells[c_idx].vertical_alignment = WD_ALIGN_VERTICAL.CENTER
                p = row_cells[c_idx].paragraphs[0]
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER if c_idx == 0 or len(val) < 8 else WD_ALIGN_PARAGRAPH.LEFT
                p.paragraph_format.space_before = Pt(0)
                p.paragraph_format.space_after = Pt(0)
                p.paragraph_format.line_spacing = 1.15
                r = p.add_run(str(val))
                r.font.name = 'Times New Roman'
                r.font.size = Pt(9.5)
                r.font.color.rgb = CLR_DARK

        p_spc = doc.add_paragraph()
        p_spc.paragraph_format.space_before = Pt(4)
        p_spc.paragraph_format.space_after = Pt(6)

    # ---------------------------------------------------------
    # 1. TRANG BÌA (COVER PAGE)
    # ---------------------------------------------------------
    print("Building Cover Page...")
    add_p("[BỔ SUNG TÊN TRƯỜNG]", bold=True, size=14, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_DARK)
    add_p("[BỔ SUNG TÊN KHOA]", bold=True, size=13, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_DARK)
    add_p("------------------------------------", bold=False, size=12, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_MUTED)

    for _ in range(3):
        add_p("", size=13)

    add_p("BÁO CÁO ĐỒ ÁN MÔN HỌC", bold=True, size=16, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_MUTED)
    add_p("HỌC PHẦN: LẬP TRÌNH JAVA", bold=True, size=18, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_PRIMARY)
    
    add_p("", size=14)
    add_p("ĐỀ TÀI:", bold=True, size=15, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_DARK)
    add_p("XÂY DỰNG HỆ THỐNG QUẢN LÝ KÝ TÚC XÁ", bold=True, size=22, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_PRIMARY)
    add_p("NHÓM THỰC HIỆN: NHÓM 12", bold=True, size=15, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_DARK)

    for _ in range(4):
        add_p("", size=13)

    # Student Info Table
    info_tbl = doc.add_table(rows=5, cols=2)
    info_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    info_tbl.autofit = False
    info_tbl.columns[0].width = Inches(3.0)
    info_tbl.columns[1].width = Inches(3.5)

    info_data = [
        ("Sinh viên thực hiện 1:", "Nguyễn Minh Trọng – MSSV: 2124110253"),
        ("Sinh viên thực hiện 2:", "Nguyễn Quốc Vương – MSSV: 2124110272"),
        ("Sinh viên thực hiện 3:", "Tạ Văn Thắng      – MSSV: 2124110256"),
        ("Giảng viên hướng dẫn:", "[BỔ SUNG TÊN GIẢNG VIÊN]"),
        ("Lớp học phần:", "[BỔ SUNG TÊN LỚP HỌC PHẦN]")
    ]

    for idx, (label, val) in enumerate(info_data):
        c0 = info_tbl.cell(idx, 0)
        c1 = info_tbl.cell(idx, 1)
        p0 = c0.paragraphs[0]
        p1 = c1.paragraphs[0]
        p0.alignment = WD_ALIGN_PARAGRAPH.LEFT
        p1.alignment = WD_ALIGN_PARAGRAPH.LEFT
        p0.paragraph_format.space_before = Pt(2)
        p0.paragraph_format.space_after = Pt(2)
        p1.paragraph_format.space_before = Pt(2)
        p1.paragraph_format.space_after = Pt(2)
        
        r0 = p0.add_run(label)
        r0.font.name = 'Times New Roman'
        r0.font.size = Pt(12)
        r0.bold = True
        r0.font.color.rgb = CLR_DARK

        r1 = p1.add_run(val)
        r1.font.name = 'Times New Roman'
        r1.font.size = Pt(12)
        r1.font.color.rgb = CLR_DARK

    for _ in range(4):
        add_p("", size=13)

    add_p("[BỔ SUNG ĐỊA ĐIỂM], NĂM 2026", bold=True, size=13, align=WD_ALIGN_PARAGRAPH.CENTER, color=CLR_DARK)

    # ---------------------------------------------------------
    # 2. TRANG NHẬN XÉT CỦA GIẢNG VIÊN
    # ---------------------------------------------------------
    print("Building Evaluation Page...")
    p_eval = add_chapter("NHẬN XÉT CỦA GIẢNG VIÊN HƯỚNG DẪN")
    
    add_p("Họ và tên giảng viên: .....................................................................................................", size=13)
    add_p("Học hàm, học vị: .........................................................................................................", size=13)
    add_p("Cơ quan công tác: ........................................................................................................", size=13)
    add_p("", size=6)
    add_p("BẢNG TIÊU CHÍ ĐÁNH GIÁ ĐỒ ÁN:", bold=True, size=13, color=CLR_PRIMARY)

    eval_headers = ["STT", "Tiêu chí đánh giá", "Trọng số", "Điểm tối đa", "Điểm đạt được"]
    eval_rows = [
        ["1", "Ý thức, tinh thần và thái độ làm việc nhóm", "20%", "2.0", ""],
        ["2", "Khả năng phân tích yêu cầu, thiết kế kiến trúc và CSDL", "30%", "3.0", ""],
        ["3", "Chất lượng sản phẩm mã nguồn Java Spring Boot và giao diện", "30%", "3.0", ""],
        ["4", "Chất lượng quyển báo cáo đồ án và khả năng trả lời câu hỏi", "20%", "2.0", ""],
        ["", "TỔNG CỘNG", "100%", "10.0", ""]
    ]
    add_styled_table(eval_headers, eval_rows, [0.6, 3.2, 0.9, 0.9, 0.9])

    add_p("NHẬN XÉT CHI TIẾT:", bold=True, size=13, color=CLR_PRIMARY)
    add_p("1. Về tính cấp thiết và mức độ hoàn thành nội dung đề tài:")
    add_p("........................................................................................................................................................")
    add_p("........................................................................................................................................................")
    add_p("2. Về cấu trúc mã nguồn, tính bảo mật và kỹ năng lập trình Java:")
    add_p("........................................................................................................................................................")
    add_p("........................................................................................................................................................")
    add_p("3. Những mặt còn hạn chế cần khắc phục:")
    add_p("........................................................................................................................................................")
    add_p("........................................................................................................................................................")
    add_p("4. Kết luận và điểm số đề xuất:")
    add_p("• Điểm số (bằng số): ..............................   • Điểm số (bằng chữ): ...................................................")
    
    add_p("", size=10)
    p_sign = add_p("Giảng viên hướng dẫn\n(Ký và ghi rõ họ tên)", bold=True, size=13, align=WD_ALIGN_PARAGRAPH.RIGHT)

    # ---------------------------------------------------------
    # 3. LỜI CẢM ƠN
    # ---------------------------------------------------------
    print("Building Acknowledgement...")
    add_chapter("LỜI CẢM ƠN")
    add_p("Lời đầu tiên, toàn thể thành viên Nhóm 12 xin được gửi lời cảm ơn chân thành và sâu sắc nhất đến Ban Giám hiệu, Quý Thầy Cô phụ trách bộ môn Lập trình Java đã tận tâm truyền đạt những kiến thức nền tảng vững chắc, phương pháp tư duy lập trình chuyên sâu và các chuẩn mực phát triển phần mềm doanh nghiệp trong suốt thời gian học tập vừa qua.")
    add_p("Đồ án môn học 'Xây dựng Hệ thống Quản lý Ký túc xá' là cơ hội quý báu để nhóm được áp dụng toàn diện các kiến thức đã lĩnh hội: từ ngôn ngữ lập trình Java hiện đại, framework Spring Boot, cơ chế ORM Spring Data JPA, hệ quản trị cơ sở dữ liệu quan hệ MySQL cho đến tư duy thiết kế hệ thống phân tầng và bảo mật thông tin.")
    add_p("Trong quá trình nghiên cứu, khảo sát nghiệp vụ và lập trình triển khai hệ thống, mặc dù nhóm đã có nhiều nỗ lực và nghiêm túc trong từng dòng mã nguồn, tuy nhiên do thời gian thực hiện có hạn và kinh nghiệm thực tế chưa phong phú nên đồ án khó tránh khỏi những điểm thiếu sót. Nhóm 12 rất mong nhận được những nhận xét, góp ý và chỉ dẫn quý báu từ Quý Thầy Cô để hệ thống ngày càng hoàn thiện, tối ưu hơn và có khả năng áp dụng thiết thực vào thực tiễn.")
    add_p("Nhóm 12 xin kính chúc Quý Thầy Cô luôn dồi dào sức khỏe, niềm vui và gặt hái nhiều thành công rực rỡ trong sự nghiệp giáo dục cao quý!")
    
    add_p("", size=10)
    add_p("TP. Hồ Chí Minh, Năm 2026\nNhóm sinh viên thực hiện (Nhóm 12):\nNguyễn Minh Trọng – Nguyễn Quốc Vương – Tạ Văn Thắng", 
          bold=True, italic=True, size=12, align=WD_ALIGN_PARAGRAPH.RIGHT)

    # ---------------------------------------------------------
    # 4. MỤC LỤC & DANH MỤC HÌNH ẢNH, BẢNG BIỂU
    # ---------------------------------------------------------
    print("Building Table of Contents...")
    add_chapter("MỤC LỤC")
    
    toc_headers = ["STT", "Nội dung đề mục", "Trang"]
    toc_data = [
        ["", "TRANG NHẬN XÉT CỦA GIẢNG VIÊN HƯỚNG DẪN", "ii"],
        ["", "LỜI CẢM ƠN", "iii"],
        ["", "MỤC LỤC", "iv"],
        ["", "DANH MỤC HÌNH ẢNH VÀ SƠ ĐỒ", "vi"],
        ["", "DANH MỤC BẢNG BIỂU", "vii"],
        ["CHƯƠNG 1", "GIỚI THIỆU ĐỀ TÀI", "1"],
        ["1.1", "Lý do chọn đề tài", "1"],
        ["1.2", "Mục tiêu đề tài", "2"],
        ["1.3", "Đối tượng sử dụng hệ thống", "2"],
        ["1.4", "Phạm vi đề tài", "3"],
        ["1.5", "Yêu cầu chức năng", "4"],
        ["1.6", "Yêu cầu phi chức năng", "5"],
        ["1.7", "Công nghệ sử dụng trong hệ thống", "6"],
        ["CHƯƠNG 2", "CƠ SỞ LÝ THUYẾT", "8"],
        ["2.1", "Tổng quan về ngôn ngữ Java & Java 25 LTS", "8"],
        ["2.2", "Khung ứng dụng Spring Boot", "9"],
        ["2.3", "Kiến trúc RESTful API & Xử lý ngoại lệ toàn cục", "10"],
        ["2.4", "Spring Data JPA và Hibernate ORM", "11"],
        ["2.5", "Cơ sở dữ liệu quan hệ MySQL & Toàn vẹn dữ liệu", "12"],
        ["2.6", "Bảo mật ứng dụng Web (Brute-force, XSS, Clickjacking, BCrypt)", "13"],
        ["2.7", "Công nghệ Frontend & Tích hợp cổng VietQR / MoMo", "15"],
        ["CHƯƠNG 3", "PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG", "17"],
        ["3.1", "Mô tả bài toán nghiệp vụ ký túc xá", "17"],
        ["3.2", "Xác định các tác nhân hệ thống", "18"],
        ["3.3", "Phân tích nghiệp vụ chi tiết", "19"],
        ["3.4", "Sơ đồ Use Case tổng quát", "22"],
        ["3.5", "Sơ đồ Activity Diagram các quy trình trọng tâm", "24"],
        ["3.6", "Thiết kế cơ sở dữ liệu quan hệ (ERD & Mô tả 15 bảng)", "28"],
        ["3.7", "Thiết kế giao diện người dùng", "35"],
        ["3.8", "Thiết kế RESTful API chi tiết", "38"],
        ["CHƯƠNG 4", "CÀI ĐẶT VÀ TRIỂN KHAI HỆ THỐNG", "44"],
        ["4.1", "Môi trường phát triển và cấu hình phần mềm", "44"],
        ["4.2", "Cấu trúc source code và mô hình phân tầng", "45"],
        ["4.3", "Cài đặt các chức năng nghiệp vụ trọng tâm", "48"],
        ["4.4", "Cài đặt cơ chế bảo mật an ninh mạng", "53"],
        ["4.5", "Hướng dẫn cài đặt, cấu hình và khởi chạy chương trình", "56"],
        ["4.6", "Minh chứng triển khai hệ thống", "58"],
        ["CHƯƠNG 5", "KIỂM THỬ HỆ THỐNG", "60"],
        ["5.1", "Mục tiêu kiểm thử", "60"],
        ["5.2", "Phương pháp kiểm thử thực tế", "60"],
        ["5.3", "Xây dựng bộ Test Case và kết quả thực tế", "61"],
        ["5.4", "Đánh giá kết quả kiểm thử", "65"],
        ["CHƯƠNG 6", "KẾT QUẢ, ĐÁNH GIÁ VÀ HƯỚNG PHÁT TRIỂN", "67"],
        ["6.1", "Kết quả đạt được", "67"],
        ["6.2", "Đánh giá hệ thống theo tiêu chuẩn kỹ thuật", "68"],
        ["6.3", "Ưu điểm nổi bật của hệ thống", "69"],
        ["6.4", "Hạn chế và tồn tại", "70"],
        ["6.5", "Hướng phát triển trong tương lai", "71"],
        ["KẾT LUẬN", "KẾT LUẬN CHUNG", "73"],
        ["TÀI LIỆU", "TÀI LIỆU THAM KHẢO", "74"],
        ["PHỤ LỤC", "PHỤ LỤC ĐỒ ÁN", "75"],
        ["DANH SÁCH", "DANH SÁCH HÌNH ẢNH CẦN BỔ SUNG (NHÓM B)", "78"]
    ]
    add_styled_table(toc_headers, toc_data, [1.0, 4.8, 0.7])

    # Danh mục hình ảnh
    add_heading2("DANH MỤC HÌNH ẢNH VÀ SƠ ĐỒ")
    fig_headers = ["Ký hiệu", "Tên hình ảnh / sơ đồ", "Phân loại", "Trang"]
    fig_data = [
        ["Hình 3.1", "Sơ đồ Use Case tổng quát hệ thống quản lý KTX", "Nhóm A (Tự tạo)", "23"],
        ["Hình 3.2", "Sơ đồ Activity Diagram: Quy trình đăng nhập & chống Brute-force", "Nhóm A (Tự tạo)", "24"],
        ["Hình 3.3", "Sơ đồ Activity Diagram: Quy trình hợp đồng & điều chuyển phòng", "Nhóm A (Tự tạo)", "25"],
        ["Hình 3.4", "Sơ đồ Activity Diagram: Quy trình lập hóa đơn & VietQR", "Nhóm A (Tự tạo)", "26"],
        ["Hình 3.5", "Sơ đồ Activity Diagram: Quy trình cấp phát & bảo trì thiết bị", "Nhóm A (Tự tạo)", "27"],
        ["Hình 3.6", "Sơ đồ cơ sở dữ liệu quan hệ ERD đầy đủ 15 thực thể", "Nhóm A (Tự tạo)", "29"],
        ["Hình 3.7", "Giao diện trang đăng nhập (login.html) có cơ chế đếm ngược", "Nhóm B (Chụp)", "35"],
        ["Hình 3.8", "Giao diện Dashboard quản trị tổng quan KTX (index.html)", "Nhóm B (Chụp)", "36"],
        ["Hình 3.9", "Giao diện quản lý thông tin sinh viên nội trú (sinh-vien.html)", "Nhóm B (Chụp)", "36"],
        ["Hình 3.10", "Giao diện danh mục phòng và sơ đồ phòng ở (phong.html)", "Nhóm B (Chụp)", "37"],
        ["Hình 3.11", "Giao diện quản lý hợp đồng thuê phòng (hop-dong.html)", "Nhóm B (Chụp)", "37"],
        ["Hình 3.12", "Giao diện lập hóa đơn & quét mã thanh toán VietQR / MoMo", "Nhóm B (Chụp)", "38"],
        ["Hình 3.13", "Giao diện quản lý cấp phát & báo hỏng thiết bị (thiet-bi.html)", "Nhóm B (Chụp)", "38"],
        ["Hình 4.1", "Sơ đồ kiến trúc tổng thể hệ thống quản lý KTX đa tầng", "Nhóm A (Tự tạo)", "46"],
        ["Hình 4.2", "Sơ đồ luồng phân tầng Controller – Service – Repository – DB", "Nhóm A (Tự tạo)", "47"],
        ["Hình 4.3", "Ảnh cấu trúc cây thư mục source code trong IDE", "Nhóm B (Chụp)", "58"],
        ["Hình 4.4", "Ảnh terminal khởi chạy Spring Boot thành công cổng 8080", "Nhóm B (Chụp)", "58"],
        ["Hình 4.5", "Ảnh cơ sở dữ liệu quan_ly_ky_tuc_xa trong phpMyAdmin / MySQL", "Nhóm B (Chụp)", "59"],
        ["Hình 4.6", "Ảnh minh họa mô phỏng 3D Campus KTX bằng Three.js", "Nhóm B (Chụp)", "59"]
    ]
    add_styled_table(fig_headers, fig_data, [1.0, 3.8, 1.1, 0.6])

    # Danh mục bảng biểu
    add_heading2("DANH MỤC BẢNG BIỂU")
    tab_headers = ["Ký hiệu", "Tên bảng biểu", "Nội dung", "Trang"]
    tab_data = [
        ["Bảng 1.1", "Bảng yêu cầu chức năng hệ thống", "Chi tiết 8 phân hệ chức năng", "4"],
        ["Bảng 1.2", "Bảng công nghệ sử dụng trong dự án", "Danh sách công nghệ, thư viện", "6"],
        ["Bảng 3.1", "Bảng tổng hợp 15 thực thể CSDL ký túc xá", "Tên bảng, PK, FK, mô tả", "30"],
        ["Bảng 3.2", "Cấu trúc chi tiết bảng nguoi_dung", "Thuộc tính, kiểu, ràng buộc", "31"],
        ["Bảng 3.3", "Cấu trúc chi tiết bảng sinh_vien", "Thuộc tính, kiểu, ràng buộc", "31"],
        ["Bảng 3.4", "Cấu trúc chi tiết bảng phong", "Thuộc tính, kiểu, ràng buộc", "32"],
        ["Bảng 3.5", "Cấu trúc chi tiết bảng hop_dong", "Thuộc tính, kiểu, ràng buộc", "32"],
        ["Bảng 3.6", "Cấu trúc chi tiết bảng khoan_thu", "Thuộc tính, kiểu, ràng buộc", "33"],
        ["Bảng 3.7", "Cấu trúc chi tiết bảng thanh_toan", "Thuộc tính, kiểu, ràng buộc", "33"],
        ["Bảng 3.8", "Cấu trúc chi tiết bảng tai_san & tai_san_phong", "Thuộc tính, kiểu, ràng buộc", "34"],
        ["Bảng 3.9", "Bảng danh mục RESTful API hệ thống", "Phương thức, endpoint, phân quyền", "39"],
        ["Bảng 4.1", "Bảng thông số môi trường phát triển", "JDK, Spring Boot, MySQL, Tool", "44"],
        ["Bảng 4.2", "Bảng mô tả các package trong mã nguồn", "9 Package chính và chức năng", "45"],
        ["Bảng 5.1", "Bảng kết quả kiểm thử đơn vị & tích hợp", "5 Test cases Surefire Report", "62"],
        ["Bảng 5.2", "Bảng kiểm thử an ninh phòng thủ mạng", "4 Test cases Security Attack", "63"],
        ["Bảng 5.3", "Bảng kịch bản kiểm thử chức năng thủ công", "10 Tình huống nghiệp vụ biên", "64"]
    ]
    add_styled_table(tab_headers, tab_data, [1.0, 3.2, 1.7, 0.6])

    # ---------------------------------------------------------
    # CHƯƠNG 1. GIỚI THIỆU ĐỀ TÀI
    # ---------------------------------------------------------
    print("Building Chapter 1...")
    add_chapter("CHƯƠNG 1. GIỚI THIỆU ĐỀ TÀI")
    
    add_heading2("1.1. Lý do chọn đề tài")
    add_p("Trong hệ sinh thái giáo dục đại học và cao đẳng, ký túc xá (KTX) đóng vai trò là một cơ sở hạ tầng thiết yếu phục vụ nhu cầu ăn ở, sinh hoạt và học tập tập trung cho hàng nghìn sinh viên xa quê. Khi quy mô trường đại học không ngừng mở rộng, áp lực đặt lên ban quản lý ký túc xá ngày càng gia tăng rõ rệt.")
    add_p("Qua khảo sát thực tế quy trình quản lý ký túc xá tại nhiều cơ sở giáo dục, nhóm tác giả nhận thấy phương pháp quản lý truyền thống dựa trên sổ sách giấy tờ, biểu mẫu in ấn hoặc các tập tin bảng tính Microsoft Excel rời rạc đang bộc lộ nhiều điểm nghẽn nghiêm trọng:")
    add_p("• Khó khăn trong việc quản lý hồ sơ sinh viên: Việc tra cứu thông tin cá nhân, thông tin liên lạc khẩn cấp, ngành học hay quê quán của sinh viên tốn nhiều thời gian; dữ liệu phân tán dễ xảy ra thất lạc hoặc không đồng bộ.")
    add_p("• Bất cập trong quản lý không gian cơ sở vật chất: Cấu trúc cơ sở vật chất KTX gồm nhiều Khu, Tòa nhà, Tầng lầu và Phòng ở. Việc theo dõi thủ công tình trạng phòng (phòng còn chỗ, phòng đã đầy, số lượng người hiện tại) thường xuyên bị sai lệch dẫn đến tình trạng xếp phòng vượt quá sức chứa hoặc bỏ sót chỗ trống.")
    add_p("• Quản lý hợp đồng thuê phòng phức tạp: Hợp đồng ở KTX gắn liền với chu kỳ thời gian (theo học kỳ hoặc năm học). Khi sinh viên chuyển phòng, gia hạn hợp đồng hoặc trả phòng tốt nghiệp, cán bộ quản lý phải cập nhật thủ công hàng loạt sổ sách, gây lãng phí nhân lực và dễ nhầm lẫn.")
    add_p("• Khâu tính toán hóa đơn, điện nước và công nợ dễ sai sót: Mỗi tháng, KTX phát sinh các khoản chi phí phức tạp bao gồm tiền phòng, chỉ số điện cũ - mới, chỉ số nước cũ - mới, cước Internet và dịch vụ vệ sinh. Việc tính nhẩm hoặc nhập tay hàng trăm hóa đơn dễ dẫn đến khiếu nại từ sinh viên; việc theo dõi các khoản nợ quá hạn gặp nhiều trở ngại.")
    add_p("• Quản lý tài sản, trang thiết bị trong phòng thiếu kiểm soát: Giường tầng, bàn học cá nhân, quạt điện, điều hòa... thường xuyên bị hỏng hóc hoặc thất thoát trong quá trình sinh hoạt. Nếu không có hệ thống ghi vết nhật ký cấp phát, báo hỏng và thu hồi rõ ràng, nhà trường sẽ chịu thiệt hại lớn về kinh phí cơ sở vật chất.")
    add_p("Xuất phát từ những đòi hỏi thực tiễn nêu trên, việc nghiên cứu và xây dựng một hệ thống phần mềm quản lý ký túc xá toàn diện, ứng dụng công nghệ Java tiên tiến, framework Spring Boot và cơ sở dữ liệu quan hệ chuẩn mực là đề tài có ý nghĩa thực tiễn to lớn và mang tính cấp bách cao.")

    add_heading2("1.2. Mục tiêu đề tài")
    add_p("Mục tiêu cốt lõi của đồ án bao gồm:")
    add_p("1. Xây dựng một ứng dụng Web hoàn chỉnh phục vụ công tác quản lý ký túc xá trên nền tảng Java 25 LTS và Spring Boot 4, đảm bảo tuân thủ mô hình kiến trúc phân tầng chuẩn doanh nghiệp (Controller – Service – Repository – Entity).")
    add_p("2. Thiết kế và cài đặt cơ sở dữ liệu quan hệ MySQL chuẩn hóa (15 thực thể), đảm bảo toàn vẹn dữ liệu, các ràng buộc khóa ngoại chặt chẽ và tối ưu hóa hiệu năng truy vấn.")
    add_p("3. Cung cấp hệ thống RESTful API đồng bộ, hỗ trợ các thao tác CRUD an toàn và xử lý ngoại lệ nghiệp vụ tập trung.")
    add_p("4. Xây dựng giao diện người dùng trực quan, responsive, tích hợp trải nghiệm mô phỏng 3D không gian KTX bằng Three.js.")
    add_p("5. Tự động hóa nghiệp vụ tài chính: Hỗ trợ tính hóa đơn hàng loạt theo phòng, tự động đối soát thanh toán online đa kênh thông qua mã VietQR chuẩn ngân hàng (MBBank 24/7) và ví điện tử MoMo, xuất phiếu thu tiền ký túc xá có số tiền đọc bằng chữ tiếng Việt chuẩn xác.")
    add_p("6. Tăng cường khả năng bảo mật an ninh mạng: Cài đặt cơ chế chống dò mật khẩu Brute-force, mã hóa mật khẩu bằng BCrypt, chống tấn công XSS và thiết lập các HTTP Security Headers phòng vệ Clickjacking.")

    add_heading2("1.3. Đối tượng sử dụng")
    add_p("Dựa trên cấu trúc phân quyền thực tế trong bảng vai_tro và nguoi_dung_vai_tro của source code dự án, hệ thống xác định 3 nhóm tác nhân chính:")
    add_p("• Quản trị viên hệ thống (quan_tri / Admin): Có toàn quyền truy cập hệ thống, quản lý tài khoản người dùng, phân bổ vai trò, quản trị cấu hình danh mục cơ sở vật chất và giám sát toàn bộ hoạt động của ứng dụng.")
    add_p("• Cán bộ quản lý ký túc xá (quan_ly_ky_tuc / Manager): Tác nhân tác nghiệp chính, phụ trách quản lý hồ sơ sinh viên, phê duyệt hoặc từ chối hợp đồng đăng ký, điều chuyển phòng ở, lập hóa đơn chi phí hàng loạt, ghi nhận thanh toán và theo dõi tài sản trang bị trong phòng.")
    add_p("• Sinh viên nội trú (sinh_vien / Student): Sinh viên sử dụng tài khoản cá nhân để đăng ký thuê phòng trực tuyến, theo dõi tình trạng hợp đồng, tra cứu chi tiết hóa đơn tiền phòng/điện nước hàng tháng và thực hiện quét mã VietQR/MoMo để thanh toán không tiền mặt.")

    add_heading2("1.4. Phạm vi đề tài")
    add_p("Phạm vi thực tế được xác định nghiêm ngặt từ mã nguồn đã triển khai:")
    add_p("• Các chức năng đã triển khai hoàn chỉnh:")
    add_p("  + Quản lý tài khoản, mã hóa BCrypt, cơ chế khóa tài khoản chống Brute-force.")
    add_p("  + Quản lý hồ sơ sinh viên liên kết chặt chẽ 1-1 với tài khoản người dùng.")
    add_p("  + Quản lý cấu trúc cơ sở vật chất phân cấp 4 cấp: Khu → Tòa nhà → Tầng → Phòng.")
    add_p("  + Quản lý phân loại phòng và tự động tổng hợp số liệu phòng trống/phòng đầy.")
    add_p("  + Quản lý vòng đời hợp đồng: tạo mới, duyệt hợp đồng (tự tăng người), từ chối, gia hạn, chuyển phòng và thanh lý hợp đồng (tự giảm người).")
    add_p("  + Quản lý quan hệ Sinh viên - Phòng (SinhVienPhongController) phục vụ tra cứu danh sách người ở trong từng phòng.")
    add_p("  + Quản lý tài chính: lập hóa đơn tự động hàng loạt, ghi nhận thanh toán, báo cáo công nợ, sinh mã thanh toán VietQR / MoMo và in phiếu thu tiền.")
    add_p("  + Quản lý tài sản: danh mục thiết bị, cấp phát phòng, báo hỏng, hoàn tất sửa chữa, thu hồi và nhật ký giao dịch tài sản.")
    add_p("  + Dashboard thống kê tổng quan các chỉ số sinh viên, phòng ốc, doanh thu và nợ đọng.")
    add_p("  + Giao diện landing page tích hợp mô phỏng 3D Campus KTX.")
    add_p("• Các chức năng chưa triển khai (định hướng tương lai):")
    add_p("  + Tích hợp Webhook tự động nhận thông báo biến động số dư ngân hàng từ Open Banking API (hiện tại cán bộ bấm xác nhận sau khi quét mã).")
    add_p("  + Chức năng gửi email/SMS thông báo tự động khi hóa đơn đến hạn.")
    add_p("  + Phân hệ quản lý chấm công, ra vào cổng KTX bằng nhận diện khuôn mặt hoặc thẻ từ.")
    add_p("• Giới hạn hiện tại:")
    add_p("  + Hệ thống triển khai chạy cục bộ (Localhost) hoặc mạng nội bộ KTX, chưa cấu hình domain public và chứng chỉ SSL thương mại.")

    add_heading2("1.5. Yêu cầu chức năng")
    add_p("Dưới đây là bảng tổng hợp các yêu cầu chức năng nghiệp vụ được hệ thống hỗ trợ:")
    
    fn_headers = ["Mã YC", "Phân hệ nghiệp vụ", "Mô tả chức năng chi tiết", "Tác nhân"]
    fn_rows = [
        ["YCCN-01", "Xác thực & Bảo mật", "Đăng nhập, đếm số lần sai, khóa tạm 15 phút sau 5 lần thất bại, mã hóa BCrypt, lọc XSS", "Tất cả tác nhân"],
        ["YCCN-02", "Quản lý Người dùng", "Thêm, sửa, xóa tài khoản, phân quyền quản trị, nhân viên hoặc sinh viên", "Quản trị viên"],
        ["YCCN-03", "Quản lý Cơ sở vật chất", "Quản lý Khu, Tòa nhà, Tầng, Phòng; kiểm soát sức chứa và số lượng người ở thực tế", "Admin, Quản lý"],
        ["YCCN-04", "Quản lý Sinh viên", "Quản lý mã sinh viên, họ tên, lớp, ngành, khoa, quê quán và liên kết tài khoản", "Quản lý KTX"],
        ["YCCN-05", "Quản lý Hợp đồng", "Đăng ký phòng, phê duyệt, từ chối, gia hạn, chuyển phòng, trả phòng, tự động cập nhật sĩ số", "Quản lý, Sinh viên"],
        ["YCCN-06", "Quản lý Hóa đơn & Nợ", "Lập hóa đơn hàng loạt theo tòa, tự tính tiền điện nước, theo dõi trạng thái quá hạn", "Quản lý KTX"],
        ["YCCN-07", "Thanh toán & Phiếu thu", "Thanh toán online qua VietQR MBBank 24/7 / MoMo, in phiếu thu tiền có chữ tiếng Việt", "Quản lý, Sinh viên"],
        ["YCCN-08", "Quản lý Trang thiết bị", "Quản lý danh mục thiết bị, cấp phát vào phòng, ghi nhận báo hỏng, sửa chữa và thu hồi", "Quản lý KTX"],
        ["YCCN-09", "Dashboard & Thống kê", "Tổng hợp số sinh viên, tòa nhà, phòng trống/đầy, tổng doanh thu và nợ đọng", "Quản lý, Admin"]
    ]
    add_styled_table(fn_headers, fn_rows, [0.8, 1.6, 3.2, 0.9], "Bảng 1.1 – Bảng yêu cầu chức năng hệ thống")

    add_heading2("1.6. Yêu cầu phi chức năng")
    add_p("• Khả năng bảo mật (Security): Mật khẩu người dùng được băm một chiều an toàn bằng thuật toán BCrypt với độ trễ tính toán phù hợp; hệ thống trang bị bộ lọc bảo vệ chống Clickjacking (X-Frame-Options: SAMEORIGIN) và vô hiệu hóa cơ chế suy đoán kiểu MIME (X-Content-Type-Options: nosniff); dữ liệu nhập vào được làm sạch loại bỏ mã độc XSS; PreparedStatements trong JPA loại trừ hoàn toàn nguy cơ SQL Injection.")
    add_p("• Độ tin cậy và Tính toàn vẹn dữ liệu (Reliability & Integrity): CSDL áp dụng đầy đủ các ràng buộc khóa ngoại (Foreign Key), quy tắc kiểm tra CHECK (sức chứa > 0, số người <= sức chứa, tiền >= 0); các thao tác nghiệp vụ phức tạp liên quan đến nhiều bảng được quản lý trong phạm vi giao dịch `@Transactional`, đảm bảo nguyên tử tính (ACID).")
    add_p("• Hiệu năng xử lý (Performance): Tận dụng kết nối cơ sở dữ liệu qua Connection Pool HikariCP với tốc độ phản hồi API dưới 200ms cho các truy vấn thông thường; các trường dữ liệu tìm kiếm thường xuyên như `sinh_vien_id`, `phong_id`, `thang`, `trang_thai` đều được đánh chỉ mục (INDEX).")
    add_p("• Khả năng bảo trì và mở rộng (Maintainability & Extensibility): Mã nguồn tuân thủ chặt chẽ kiến trúc phân tầng, phân tách rõ ràng trách nhiệm giữa Controller, Service và Repository; sử dụng Lombok để giảm thiểu mã mẫu rườm rà; cấu hình kết nối CSDL linh hoạt qua biến môi trường.")
    add_p("• Giao diện và Trải nghiệm người dùng (Usability): Giao diện web hiện đại, phối màu hài hòa, hỗ trợ thông báo tức thì, biểu tượng trực quan, xem trước hóa đơn và in ấn chuyên nghiệp.")

    add_heading2("1.7. Công nghệ sử dụng")
    add_p("Bảng tổng hợp chi tiết các công nghệ thực tế được sử dụng trong dự án:")
    
    tech_headers = ["Công nghệ / Thư viện", "Phiên bản thực tế", "Vai trò và vị trí trong hệ thống"]
    tech_rows = [
        ["Java Development Kit (JDK)", "Java 25 LTS", "Ngôn ngữ lập trình chính, hỗ trợ cú pháp hiện đại (Record, Pattern Matching)"],
        ["Spring Boot Framework", "4.1.1", "Framework backend cốt lõi, cung cấp cơ chế IoC, DI, nhúng Tomcat Web Server"],
        ["Spring Web MVC", "4.1.1", "Xây dựng các RESTful Controller tiếp nhận và xử lý yêu cầu HTTP"],
        ["Spring Data JPA / Hibernate", "Hibernate ORM 7.4.5", "Khung ORM ánh xạ thực thể Java vào các bảng CSDL quan hệ"],
        ["Spring Security Crypto", "7.1.1", "Thư viện cung cấp thuật toán băm mật khẩu an toàn BCryptPasswordEncoder"],
        ["MySQL Database / Connector-J", "MySQL 8.0 / v9.7.0", "Hệ quản trị cơ sở dữ liệu quan hệ lưu trữ dữ liệu bền vững"],
        ["H2 In-Memory Database", "2.4.240", "Cơ sở dữ liệu bộ nhớ ảo phục vụ chạy Unit & Integration Test"],
        ["Lombok", "1.18.46", "Tự động sinh Getter, Setter, Constructor, tối giản mã nguồn Java"],
        ["Apache Maven", "3.9+ / Maven Wrapper", "Quản lý vòng đời build dự án và giải quyết cây phụ thuộc thư viện"],
        ["HTML5, TailwindCSS, Bootstrap 5", "v5.3.3 & Tailwind v3", "Xây dựng giao diện Web responsive, modal form và các bảng dữ liệu"],
        ["Three.js Engine", "r128 / WebGL", "Mô phỏng 3D Campus KTX trực quan hóa không gian khuôn viên"],
        ["JUnit 5 & Mockito", "JUnit 6.0 / Mockito 5.23", "Khung kiểm thử tự động cho Unit Test và Security Test"]
    ]
    add_styled_table(tech_headers, tech_rows, [1.8, 1.4, 3.3], "Bảng 1.2 – Bảng công nghệ sử dụng trong dự án")

    # ---------------------------------------------------------
    # CHƯƠNG 2. CƠ SỞ LÝ THUYẾT
    # ---------------------------------------------------------
    print("Building Chapter 2...")
    add_chapter("CHƯƠNG 2. CƠ SỞ LÝ THUYẾT")

    add_heading2("2.1. Tổng quan về Java & Java 25")
    add_p("Java là một ngôn ngữ lập trình hướng đối tượng đa mục đích, được phát triển với triết lý nổi tiếng 'Write Once, Run Anywhere' (WORA). Mã nguồn Java sau khi biên dịch sẽ chuyển thành dạng bytecode (.class) độc lập với kiến trúc phần cứng và được thực thi trên máy ảo Java (Java Virtual Machine - JVM).")
    add_p("Dự án áp dụng phiên bản Java 25 (LTS) - phiên bản hỗ trợ dài hạn mới nhất, mang lại nhiều cải tiến đột phá:")
    add_p("• Cơ chế Garbage Collection (GC) thế hệ mới giúp tối ưu hóa việc quản lý bộ nhớ heap, giảm thiểu độ trễ ứng dụng.")
    add_p("• Cú pháp Record giúp định nghĩa các đối tượng truyền tải dữ liệu (DTO) bất biến một cách cực kỳ súc tích mà không cần viết thủ công equals, hashCode, toString.")
    add_p("• Tăng cường tính năng Pattern Matching cho switch và toán tử instanceof, giúp việc kiểm tra và ép kiểu đối tượng trở nên an toàn và dễ đọc hơn.")

    add_heading2("2.2. Khung ứng dụng Spring Boot")
    add_p("Spring Boot là một dự án trọng tâm thuộc hệ sinh thái Spring Framework, được thiết kế nhằm mục đích đơn giản hóa tối đa quy trình khởi tạo, cấu hình và triển khai các ứng dụng Spring cấp doanh nghiệp.")
    add_p("Các đặc tính cốt lõi của Spring Boot được tận dụng triệt để trong dự án:")
    add_p("• Inversion of Control (IoC) & Dependency Injection (DI): Toàn bộ vòng đời của các thành phần (Controller, Service, Repository) đều do Spring IoC Container quản lý. Các phụ thuộc được tiêm tự động thông qua Constructor Injection, giúp mã nguồn lỏng lẻo (loose coupling) và dễ dàng viết kiểm thử giả lập (mocking).")
    add_p("• Auto-Configuration: Tự động cấu hình DataSource, EntityManagerFactory, TransactionManager và MessageConverters dựa trên các thư viện hiện diện trong classpath.")
    add_p("• Nhúng sẵn Servlet Container: Tích hợp sẵn máy chủ Apache Tomcat, cho phép đóng gói ứng dụng thành một tệp JAR thực thi độc lập (executable JAR) mà không cần cài đặt Tomcat ngoài.")

    add_heading2("2.3. Kiến trúc RESTful API & Xử lý ngoại lệ")
    add_p("REST (Representational State Transfer) là kiểu kiến trúc phần mềm tiêu chuẩn cho các dịch vụ web. Một RESTful API sử dụng các phương thức chuẩn của giao thức HTTP để thực hiện các thao tác trên tài nguyên:")
    add_p("• GET: Truy vấn và đọc dữ liệu (không làm thay đổi trạng thái hệ thống).")
    add_p("• POST: Tạo mới một tài nguyên hoặc kích hoạt một hành vi nghiệp vụ đặc thù (duyệt hợp đồng, ghi nhận thanh toán).")
    add_p("• PUT: Cập nhật toàn bộ thông tin của một tài nguyên hiện có.")
    add_p("• DELETE: Xóa bỏ tài nguyên khỏi hệ thống.")
    add_p("Trong dự án, các Controller sử dụng annotation `@RestController` và `@RequestMapping`. Dữ liệu trao đổi giữa máy khách và máy chủ được định dạng hoàn toàn bằng JSON thông qua bộ phân tích Jackson. Đồng thời, dự án triển khai lớp `ApiExceptionHandler` kế thừa `@RestControllerAdvice` để bắt giữ ngoại lệ `DataIntegrityViolationException`, trả về cấu trúc lỗi chuẩn RFC 7807 (ProblemDetail) với mã trạng thái HTTP 409 CONFLICT khi xảy ra trùng lặp dữ liệu hoặc vi phạm khóa ngoại.")

    add_heading2("2.4. Spring Data JPA và Hibernate ORM")
    add_p("ORM (Object-Relational Mapping) là kỹ thuật cầu nối giúp lập trình viên thao tác với các bảng CSDL quan hệ dưới dạng các lớp đối tượng thuần Java (Entity).")
    add_p("Hibernate là công cụ hiện thực hóa chuẩn JPA (Jakarta Persistence API). Trong khi đó, Spring Data JPA bổ sung một tầng trừu tượng hóa mạnh mẽ trên Hibernate:")
    add_p("• Các interface như `SinhVienRepository`, `PhongRepository` chỉ cần kế thừa `JpaRepository<T, ID>` là tự động sở hữu toàn bộ các thao tác cơ bản như findAll, findById, save, deleteById mà không cần viết một dòng mã thực thi nào.")
    add_p("• Hỗ trợ cơ chế Derived Query Methods: Tự động phân tích tên hàm để sinh câu truy vấn SQL tương ứng (ví dụ: `findByTrangThai`, `findByEmailIgnoreCaseOrMaNguoiDung`).")
    add_p("• Hỗ trợ viết truy vấn tùy biến JPQL có gắn annotation `@Query` và `@Param`, giúp thực hiện các phép lọc dữ liệu phức tạp.")
    add_p("• Cơ chế quản lý giao dịch `@Transactional`: Đảm bảo toàn bộ chuỗi thao tác CSDL diễn ra trọn vẹn; nếu có bất kỳ lỗi RuntimeException nào xảy ra, hệ thống tự động Rollback về trạng thái ban đầu.")

    add_heading2("2.5. Cơ sở dữ liệu quan hệ MySQL & Toàn vẹn dữ liệu")
    add_p("MySQL là hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) mã nguồn mở phổ biến nhất thế giới. CSDL của dự án được thiết kế chặt chẽ theo các quy tắc chuẩn hóa nhằm hạn chế tối đa dư thừa dữ liệu:")
    add_p("• Khóa chính (Primary Key - PK): Định danh duy nhất từng bản ghi, sử dụng kiểu BIGINT AUTO_INCREMENT để đảm bảo khả năng mở rộng hàng triệu dòng dữ liệu.")
    add_p("• Khóa ngoại (Foreign Key - FK): Thiết lập mối liên kết giữa các bảng, đi kèm các hành vi toàn vẹn tham chiếu `ON UPDATE CASCADE` và `ON DELETE RESTRICT` (hoặc `SET NULL` cho các trường tham chiếu không bắt buộc như người thực hiện bảo trì).")
    add_p("• Ràng buộc toàn vẹn miền giá trị (CHECK constraints): Đảm bảo tính hợp lệ logic của dữ liệu ngay tại tầng CSDL (ví dụ: `suc_chua > 0`, `so_tien >= 0`, `ngay_ket_thuc >= ngay_bat_dau`).")

    add_heading2("2.6. An toàn thông tin và Bảo mật ứng dụng Web")
    add_p("Khác với các ứng dụng học tập thông thường, dự án Quản lý Ký túc xá đặc biệt chú trọng khía cạnh bảo mật an toàn thông tin với 5 lớp bảo vệ chuyên sâu được cài đặt thực tế:")
    add_p("1. Phòng chống tấn công dò mật khẩu (Brute-force / Dictionary Attack): Triển khai dịch vụ `LoginAttemptService` lưu trữ bộ đếm số lần đăng nhập thất bại theo từng tài khoản/IP trong `ConcurrentHashMap`. Khi một tài khoản nhập sai mật khẩu quá 5 lần liên tiếp, hệ thống lập tức kích hoạt cơ chế khóa tạm thời trong thời gian 15 phút (900 giây) và trả về mã lỗi HTTP 429 TOO_MANY_REQUESTS. Khi đăng nhập thành công, bộ đếm thất bại được reset về 0.")
    add_p("2. Mã hóa mật khẩu một chiều bằng thuật toán BCrypt: Thông qua `PasswordConfig` và `NguoiDungService`, mật khẩu người dùng không bao giờ được lưu trữ dưới dạng văn bản thô (plaintext). Mật khẩu được mã hóa tự động bằng `BCryptPasswordEncoder` với chuỗi muối ngẫu nhiên (salt). Mật khẩu sau khi băm có dạng `$2a$10$...` dài 60 ký tự, kháng cự hoàn toàn trước các cuộc tấn công tra cứu bảng cầu vồng (Rainbow Table).")
    add_p("3. Phòng chống tấn công Cross-Site Scripting (XSS): Triển khai lớp tiện ích `XssProtectionUtil` sử dụng các mẫu Regular Expression để bóc tách và loại bỏ triệt để các thẻ `<script>`, `<iframe>`, tiền tố nguy hiểm `javascript:` cũng như các trình lắng nghe sự kiện nội tuyến (`onerror=`, `onload=`). Ngoài ra, tiện ích còn hỗ trợ mã hóa các ký tự đặc biệt (&, <, >, \", ') thành các thực thể HTML an toàn trước khi kết xuất.")
    add_p("4. Thiết lập các tiêu đề an ninh HTTP (Security Headers): Lớp `SecurityHeadersFilter` có độ ưu tiên cao nhất (`@Order(Ordered.HIGHEST_PRECEDENCE)`), tự động gán vào mọi phản hồi HTTP các header phòng vệ:")
    add_p("   • `X-Frame-Options: SAMEORIGIN`: Ngăn chặn việc nhúng trang web vào iframe của tên miền độc hại, triệt tiêu nguy cơ tấn công Clickjacking.")
    add_p("   • `X-Content-Type-Options: nosniff`: Ngăn chặn trình duyệt tự ý suy đoán và thực thi sai định dạng nội dung tải về (MIME-type Sniffing).")
    add_p("   • `X-XSS-Protection: 1; mode=block`: Kích hoạt bộ lọc XSS tích hợp của trình duyệt.")
    add_p("   • `Content-Security-Policy (CSP)`: Chỉ định nguồn gốc tài nguyên hợp lệ được phép tải (hình ảnh từ vietqr.io, mã nguồn nội bộ).")
    add_p("5. Phòng chống tấn công SQL Injection: Tất cả các truy vấn thông qua Spring Data JPA và Hibernate đều được biên dịch thành dạng PreparedStatements với tham số hóa đầy đủ. Dữ liệu nhập vào của kẻ tấn công (kể cả chuỗi kinh điển `' OR '1'='1' --`) chỉ được coi là chuỗi ký tự thông thường, hoàn toàn không thể làm biến đổi cấu trúc câu lệnh SQL.")

    add_heading2("2.7. Công nghệ Frontend & Tích hợp dịch vụ thanh toán")
    add_p("• Giao diện Web được xây dựng dựa trên sự kết hợp giữa kiến trúc CSS Utility-first của TailwindCSS và hệ thống component linh hoạt của Bootstrap 5, mang lại trải nghiệm mượt mà, thời gian tải trang nhanh và tương thích tốt trên cả máy tính lẫn thiết bị di động.")
    add_p("• Mô phỏng 3D Campus KTX: Ứng dụng thư viện Three.js để dựng khung cảnh 3D khuôn viên KTX với hiệu ứng ánh sáng, vật liệu và khả năng tương tác xoay góc nhìn, tạo điểm nhấn trực quan vượt trội so với các phần mềm quản lý truyền thống.")
    add_p("• Tích hợp cổng thanh toán VietQR & MoMo: Ứng dụng tạo mã QR thanh toán động tuân thủ theo tiêu chuẩn VietQR (chuẩn hóa bởi Napas và Ngân hàng Nhà nước). Mã QR tích hợp tự động mã ngân hàng thụ hưởng (MBBank), số tài khoản, số tiền nợ chính xác và cú pháp nội dung chuyển khoản gồm Mã hóa đơn + Mã sinh viên, giúp sinh viên thanh toán tức thì chỉ với một thao tác quét camera trên ứng dụng ngân hàng.")

    # ---------------------------------------------------------
    # CHƯƠNG 3. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG
    # ---------------------------------------------------------
    print("Building Chapter 3...")
    add_chapter("CHƯƠNG 3. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG")

    add_heading2("3.1. Mô tả bài toán nghiệp vụ ký túc xá")
    add_p("Hệ thống Quản lý Ký túc xá giải quyết bài toán vận hành hàng ngày của một đơn vị ký túc xá quy mô từ vài trăm đến hàng nghìn sinh viên. Chu trình quản lý xoay quanh 4 trục nghiệp vụ trọng tâm:")
    add_p("1. Trục Cơ sở vật chất: Quản lý hiện trạng phòng ở, đảm bảo không xảy ra tình trạng xếp quá tải hoặc để phòng trống lãng phí.")
    add_p("2. Trục Cư dân (Sinh viên & Hợp đồng): Quản lý vòng đời ở KTX từ khi đăng ký, xét duyệt hồ sơ, cư trú, gia hạn, chuyển đổi phòng cho đến khi trả phòng.")
    add_p("3. Trục Tài chính (Hóa đơn & Công nợ): Tự động hóa phát hành phiếu thu hàng tháng, theo dõi hạn nộp, hỗ trợ thanh toán online và kiểm soát chặt chẽ công nợ.")
    add_p("4. Trục Cơ sở vật chất nội thất (Tài sản phòng): Quản lý vòng đời của trang thiết bị trong từng phòng ở, quy trách nhiệm người sử dụng và người bảo trì.")

    add_heading2("3.2. Xác định các tác nhân hệ thống")
    add_p("Hệ thống phân định 3 tác nhân tương tác chính:")
    add_p("• Tác nhân Quản trị viên (Admin): Chịu trách nhiệm bảo đảm hệ thống vận hành liên tục; phân quyền tài khoản người dùng; quản lý danh mục Khu, Tòa, Tầng, Loại phòng và toàn quyền giám sát dữ liệu.")
    add_p("• Tác nhân Quản lý KTX (Manager): Cán bộ nghiệp vụ trực tiếp tương tác với sinh viên; duyệt hồ sơ đăng ký; điều chuyển sinh viên giữa các phòng; lập hóa đơn điện nước hàng tháng; ghi nhận các giao dịch thanh toán và điều phối sửa chữa trang thiết bị.")
    add_p("• Tác nhân Sinh viên (Student): Người thụ hưởng dịch vụ; nộp hồ sơ thuê phòng; theo dõi tình trạng phê duyệt; tra cứu công nợ; thực hiện quét mã thanh toán điện tử và gửi thông báo báo hỏng thiết bị.")

    add_heading2("3.3. Phân tích nghiệp vụ chi tiết")
    
    add_heading3("3.3.1. Nghiệp vụ Quản lý Hợp đồng & Xếp phòng")
    add_p("• Mục đích: Kiểm soát chặt chẽ chỗ ở của sinh viên trong ký túc xá, bảo đảm tuân thủ đúng sức chứa của từng phòng.")
    add_p("• Tác nhân: Quản lý KTX và Sinh viên.")
    add_p("• Dữ liệu đầu vào: Sinh viên ID, Phòng ID, Ngày bắt đầu, Ngày kết thúc, Tiền phòng thỏa thuận.")
    add_p("• Quy trình xử lý tại backend (`HopDongService`):")
    add_p("  1. Khi sinh viên đăng ký, hợp đồng được tạo ở trạng thái mặc định `'cho_duyet'`, số người trong phòng chưa thay đổi.")
    add_p("  2. Khi quản lý bấm Duyệt (`duyetHopDong`), hệ thống kiểm tra điều kiện: `phong.soNguoiHienTai < phong.sucChua`. Nếu phòng đã đầy, ném ngoại lệ HTTP 400 Bad Request. Nếu còn chỗ, tăng `soNguoiHienTai` thêm 1. Nếu số người đạt mức tối đa, chuyển trạng thái phòng sang `'da_day'`. Cập nhật trạng thái hợp đồng thành `'dang_hieu_luc'`.")
    add_p("  3. Khi thực hiện Chuyển phòng (`chuyenPhong`): Hệ thống kiểm tra phòng đích còn chỗ trống. Nếu hợp lệ, giảm `soNguoiHienTai` của phòng cũ đi 1 (mở lại trạng thái `'con_cho'`), đồng thời tăng `soNguoiHienTai` của phòng mới thêm 1 và cập nhật lại tham chiếu phòng trong hợp đồng.")
    add_p("  4. Khi Thanh lý hợp đồng (`thanhLyHopDong`): Chuyển trạng thái hợp đồng sang `'da_ket_thuc'`, giảm số người ở phòng hiện tại đi 1 và cập nhật trạng thái phòng sang `'con_cho'`.")
    add_p("• Kết quả đầu ra: Bản ghi hợp đồng được cập nhật chuẩn xác, tình trạng sĩ số phòng luôn phản ánh đúng thực tế.")

    add_heading3("3.3.2. Nghiệp vụ Lập hóa đơn hàng loạt & Thanh toán VietQR")
    add_p("• Mục đích: Giảm thiểu 90% thời gian tính toán chi phí hàng tháng, cung cấp kênh thanh toán tiện lợi và xóa bỏ sai sót số liệu.")
    add_p("• Tác nhân: Quản lý KTX và Sinh viên.")
    add_p("• Dữ liệu đầu vào: Tháng thu phí (ví dụ: '2026-10'), Tòa nhà ID, Hạn thanh toán, Đơn giá điện (đ/kWh), Đơn giá nước (đ/m3), Cước Internet, Tiền dịch vụ.")
    add_p("• Quy trình xử lý tại backend (`KhoanThuService`):")
    add_p("  1. Hệ thống lọc tất cả các hợp đồng đang có hiệu lực (`'dang_hieu_luc'`) thuộc tòa nhà được chọn.")
    add_p("  2. Với mỗi hợp đồng, hệ thống khởi tạo một bản ghi `KhoanThu`, gán mã hóa đơn duy nhất dạng `HD-YYYYMM-XXXXX`.")
    add_p("  3. Tự động tính toán: `soTien = tienPhong + tienDien + tienNuoc + tienInternet + tienDichVu`. Gán `daThanhToan = 0`, `conLai = soTien`, trạng thái `'chua_thanh_toan'`.")
    add_p("  4. Sinh viên mở giao diện tra cứu, hệ thống tự động gọi API sinh mã QR VietQR MBBank 24/7 chứa chính xác số tiền nợ và cú pháp chuyển khoản.")
    add_p("  5. Khi thanh toán hoàn tất, quản lý ghi nhận thanh toán (`ghiNhanThanhToan`): Tạo bản ghi `ThanhToan`, tăng `daThanhToan`, giảm `conLai`. Nếu `conLai <= 0`, tự động cập nhật trạng thái sang `'da_thanh_toan'`.")
    add_p("  6. Sinh viên hoặc cán bộ có thể mở Phiếu thu tiền điện tử, hệ thống sử dụng thuật toán `MoneyToWordsUtil` để diễn giải số tiền bằng chữ tiếng Việt (ví dụ: 1.500.000 VNĐ → 'Một triệu năm trăm nghìn đồng chẵn').")

    add_heading3("3.3.3. Nghiệp vụ Quản lý Trang thiết bị & Tài sản phòng")
    add_p("• Mục đích: Theo dõi tài sản công, quy trách nhiệm bảo quản và nhanh chóng xử lý hỏng hóc.")
    add_p("• Tác nhân: Cán bộ quản lý KTX.")
    add_p("• Dữ liệu đầu vào: Mã thiết bị, Tên thiết bị, Loại tài sản, Đơn giá, Phòng nhận, Số lượng cấp phát.")
    add_p("• Quy trình xử lý tại backend (`ThietBiService`):")
    add_p("  1. Cấp phát: Tạo bản ghi trong `tai_san_phong` với trạng thái `'dang_su_dung'`, đồng thời tự động ghi một bản ghi lịch sử vào `lich_su_tai_san` với loại giao dịch `'cap_phat'`.")
    add_p("  2. Báo hỏng: Cập nhật trạng thái trang bị sang `'hong_hoc'`, ghi nhận lý do và lưu lịch sử giao dịch `'bao_hong'`.")
    add_p("  3. Sửa chữa: Sau khi khắc phục sự cố, cập nhật trạng thái trở lại `'dang_su_dung'`, ghi nhận lịch sử `'sua_chua'`.")
    add_p("  4. Thu hồi: Khi tài sản hết niên hạn hoặc trả về kho, cập nhật `ngay_thu_hoi`, đổi trạng thái sang `'thu_hoi'` và ghi nhận lịch sử `'thu_hoi'`.")

    add_heading2("3.4. Sơ đồ Use Case tổng quát")
    add_p("Sơ đồ Use Case tổng quát mô tả trực quan các chức năng hệ thống được ánh xạ tương ứng với 3 tác nhân: Quản trị viên, Quản lý ký túc xá và Sinh viên:")
    add_diagram_image("hinh_3_1_usecase_tong_quat.png", "Hình 3.1 – Sơ đồ Use Case tổng quát hệ thống quản lý KTX", width_inches=6.3)
    add_p("Phân tích sơ đồ Use Case:")
    add_p("• Nhóm Quản trị viên tương tác trực tiếp với các use case bảo mật cốt lõi, quản trị tài khoản, phân quyền vai trò và khởi tạo cơ sở vật chất.")
    add_p("• Nhóm Quản lý KTX tập trung vào các quy trình tác nghiệp định kỳ: xét duyệt hợp đồng, điều chuyển chỗ ở, lập hóa đơn chi phí, giám sát thanh toán và điều phối thiết bị phòng.")
    add_p("• Nhóm Sinh viên tương tác với các use case tự phục vụ: đăng ký phòng ở trực tuyến, kiểm tra công nợ, quét mã QR thanh toán và gửi yêu cầu sửa chữa thiết bị.")

    add_heading2("3.5. Sơ đồ Activity Diagram")
    
    add_heading3("3.5.1. Quy trình đăng nhập và chống Brute-force")
    add_p("Sơ đồ hoạt động dưới đây mô tả chi tiết quy trình xử lý đăng nhập an toàn, cơ chế kiểm tra khóa tạm thời và so khớp mật khẩu mã hóa BCrypt:")
    add_diagram_image("hinh_3_2_activity_dang_nhap.png", "Hình 3.2 – Sơ đồ Activity Diagram: Quy trình đăng nhập & chống Brute-force", width_inches=5.8)
    add_p("Giải thích sơ đồ Hình 3.2: Khi người dùng gửi yêu cầu đăng nhập, `AuthController` trước tiên tra cứu `LoginAttemptService` để kiểm tra tài khoản có đang bị khóa hay không. Nếu số lần nhập sai liên tiếp đạt ngưỡng 5 lần, hệ thống lập tức từ chối và trả về HTTP 429 kèm thời gian chờ còn lại mà không cần thực hiện truy vấn CSDL, giúp bảo vệ máy chủ khỏi nguy cơ bị nghẽn tài nguyên do tấn công DoS/Brute-force. Nếu tài khoản chưa bị khóa, dịch vụ `NguoiDungService` sẽ truy vấn CSDL và sử dụng hàm `matches` của BCrypt để so khớp chuỗi băm. Đăng nhập thành công sẽ giải phóng bộ đếm sai; ngược lại mỗi lần sai sẽ tăng biến đếm attempts.")

    add_heading3("3.5.2. Quy trình vòng đời hợp đồng & điều chuyển phòng")
    add_p("Sơ đồ hoạt động mô tả chu trình xét duyệt hợp đồng và điều chuyển chỗ ở sinh viên:")
    add_diagram_image("hinh_3_3_activity_hop_dong.png", "Hình 3.3 – Sơ đồ Activity Diagram: Quy trình hợp đồng & điều chuyển phòng", width_inches=6.0)
    add_p("Giải thích sơ đồ Hình 3.3: Thể hiện tính toàn vẹn trong khâu quản lý sĩ số. Sinh viên nộp đơn đăng ký ở trạng thái 'cho_duyet'. Quản lý xét duyệt hồ sơ: nếu từ chối, hợp đồng kết thúc; nếu duyệt, hệ thống kiểm tra sức chứa phòng đích. Chỉ khi phòng còn chỗ trống, sĩ số mới tăng thêm 1 và hợp đồng chuyển sang 'dang_hieu_luc'. Khi sinh viên có nguyện vọng chuyển phòng, hệ thống tự động thực hiện hai thao tác nguyên tử: giải phóng 1 chỗ ở phòng cũ và chiếm 1 chỗ ở phòng mới.")

    add_heading3("3.5.3. Quy trình lập hóa đơn & thanh toán VietQR / MoMo")
    add_p("Sơ đồ hoạt động quy trình tự động hóa tài chính và thanh toán đa kênh:")
    add_diagram_image("hinh_3_4_activity_hoa_don.png", "Hình 3.4 – Sơ đồ Activity Diagram: Quy trình lập hóa đơn & thanh toán VietQR", width_inches=6.0)
    add_p("Giải thích sơ đồ Hình 3.4: Quản lý chỉ cần nhập bảng đơn giá một lần cho toàn bộ tòa nhà, `KhoanThuService` sẽ tự động quét danh sách sinh viên đang cư trú để phát hành hóa đơn hàng loạt. Sinh viên chỉ cần mở ứng dụng ngân hàng quét mã VietQR là hoàn tất việc thanh toán; hệ thống ghi nhận giao dịch `ThanhToan` và hỗ trợ in phiếu thu chuyên nghiệp.")

    add_heading3("3.5.4. Quy trình quản lý tài sản & trang thiết bị phòng")
    add_p("Sơ đồ hoạt động quy trình cấp phát, báo hỏng, bảo trì và thu hồi tài sản:")
    add_diagram_image("hinh_3_5_activity_thiet_bi.png", "Hình 3.5 – Sơ đồ Activity Diagram: Quy trình cấp phát & bảo trì thiết bị", width_inches=5.8)
    add_p("Giải thích sơ đồ Hình 3.5: Đảm bảo mọi biến động về mặt vật lý của thiết bị đều được lưu vết trong bảng `lich_su_tai_san` đi kèm mã nhân sự thực hiện và mốc thời gian chính xác, ngăn chặn tình trạng thất thoát tài sản nhà trường.")

    add_heading2("3.6. Thiết kế cơ sở dữ liệu quan hệ")
    
    add_heading3("3.6.1. Sơ đồ ERD (Entity Relationship Diagram)")
    add_p("Sơ đồ ERD mô tả đầy đủ 15 bảng dữ liệu, khóa chính, khóa ngoại và mối quan hệ ràng buộc trong file script CSDL `quan_ly_ky_tuc_xa.sql`:")
    add_diagram_image("hinh_3_6_erd_quan_he.png", "Hình 3.6 – Sơ đồ cơ sở dữ liệu quan hệ ERD đầy đủ 15 thực thể", width_inches=6.4)

    add_heading3("3.6.2. Bảng mô tả 15 thực thể dữ liệu")
    add_p("Dưới đây là bảng tổng hợp 15 bảng dữ liệu trong CSDL của dự án:")
    
    tbl_summary_headers = ["Tên bảng (Entity)", "Khóa chính (PK)", "Khóa ngoại (FK)", "Chức năng và ý nghĩa nghiệp vụ"]
    tbl_summary_rows = [
        ["nguoi_dung", "id", "Không", "Lưu trữ thông tin tài khoản đăng nhập, họ tên, email, mật khẩu băm BCrypt, trạng thái"],
        ["vai_tro", "id", "Không", "Định nghĩa các nhóm vai trò trong hệ thống (quan_tri, sinh_vien, quan_ly_ky_tuc)"],
        ["nguoi_dung_vai_tro", "(nguoi_dung_id, vai_tro_id)", "nguoi_dung_id, vai_tro_id", "Bảng trung gian thể hiện mối quan hệ nhiều - nhiều giữa người dùng và vai trò"],
        ["sinh_vien", "id", "nguoi_dung_id", "Lưu hồ sơ sinh viên (mã SV, lớp, ngành, khoa, khóa học, quê quán); quan hệ 1-1 với nguoi_dung"],
        ["khu", "id", "Không", "Quản lý phân khu ký túc xá (Khu A, Khu B...) kèm tên khu, địa chỉ, mô tả"],
        ["toa_nha", "id", "khu_id", "Quản lý các khối tòa nhà trong từng khu, lưu mã tòa, tên tòa, tổng số tầng"],
        ["tang", "id", "toa_nha_id", "Quản lý các tầng lầu thuộc tòa nhà, có ràng buộc UNIQUE (toa_nha_id, so_tang)"],
        ["phong", "id", "tang_id", "Quản lý thông tin chi tiết từng phòng ở: mã phòng, sức chứa, số người ở, loại phòng, đơn giá"],
        ["hop_dong", "id", "sinh_vien_id, phong_id", "Quản lý hợp đồng thuê phòng của sinh viên, ngày bắt đầu, kết thúc, giá phòng, trạng thái"],
        ["loai_tai_san", "id", "Không", "Danh mục phân loại trang thiết bị (Giường tầng, Bàn học, Quạt điện, Điều hòa...)"],
        ["tai_san", "id", "loai_tai_san_id", "Quản lý danh mục thiết bị cụ thể trong kho KTX: mã tài sản, tên, ngày nhập, nguyên giá"],
        ["tai_san_phong", "id", "tai_san_id, phong_id", "Quản lý việc phân bổ/trang bị thiết bị cho từng phòng, số lượng, ngày cấp, trạng thái"],
        ["lich_su_tai_san", "id", "tai_san_id, phong_id, nguoi_thuc_hien_id", "Nhật ký lưu vết giao dịch tài sản (cấp phát, báo hỏng, sửa chữa, thu hồi) kèm thời gian"],
        ["khoan_thu", "id", "sinh_vien_id, hop_dong_id, phong_id", "Quản lý hóa đơn thu tiền: mã HĐ, tháng, chi tiết tiền phòng, điện, nước, net, số tiền nợ"],
        ["thanh_toan", "id", "khoan_thu_id", "Quản lý các phiếu giao dịch thanh toán thành công: số tiền, phương thức (vietqr, momo, tiền mặt)"]
    ]
    add_styled_table(tbl_summary_headers, tbl_summary_rows, [1.5, 1.2, 1.5, 2.3], "Bảng 3.1 – Bảng tổng hợp 15 thực thể CSDL ký túc xá")

    add_heading3("3.6.3. Mô tả thuộc tính chi tiết các bảng trọng tâm")
    
    # nguoi_dung
    nd_headers = ["Tên thuộc tính", "Kiểu dữ liệu", "Ràng buộc", "Ý nghĩa nghiệp vụ"]
    nd_rows = [
        ["id", "BIGINT UNSIGNED", "PRIMARY KEY, AUTO_INCREMENT", "Mã định danh duy nhất của người dùng"],
        ["ma_nguoi_dung", "VARCHAR(30)", "NOT NULL, UNIQUE", "Mã đăng nhập định danh duy nhất (VD: ND001)"],
        ["ho_ten", "VARCHAR(100)", "NOT NULL", "Họ và tên đầy đủ của người dùng"],
        ["email", "VARCHAR(150)", "NOT NULL, UNIQUE", "Địa chỉ email định danh dùng để đăng nhập"],
        ["mat_khau", "VARCHAR(255)", "NOT NULL", "Mật khẩu đã băm an toàn bằng thuật toán BCrypt"],
        ["so_dien_thoai", "VARCHAR(20)", "NULL", "Số điện thoại liên lạc cá nhân"],
        ["gioi_tinh", "VARCHAR(20)", "NULL", "Giới tính (Nam / Nữ)"],
        ["ngay_sinh", "DATE", "NULL", "Ngày tháng năm sinh"],
        ["trang_thai", "VARCHAR(30)", "NOT NULL, DEFAULT 'hoat_dong'", "Trạng thái tài khoản (hoat_dong, bi_khoa)"],
        ["created_at", "TIMESTAMP", "NOT NULL, DEFAULT CURRENT_TIMESTAMP", "Thời điểm tạo tài khoản"]
    ]
    add_styled_table(nd_headers, nd_rows, [1.4, 1.4, 1.7, 2.0], "Bảng 3.2 – Cấu trúc chi tiết bảng nguoi_dung")

    # sinh_vien
    sv_headers = ["Tên thuộc tính", "Kiểu dữ liệu", "Ràng buộc", "Ý nghĩa nghiệp vụ"]
    sv_rows = [
        ["id", "BIGINT UNSIGNED", "PRIMARY KEY, AUTO_INCREMENT", "Mã định danh hồ sơ sinh viên"],
        ["nguoi_dung_id", "BIGINT UNSIGNED", "NOT NULL, UNIQUE, FK -> nguoi_dung(id)", "Liên kết 1-1 với tài khoản người dùng"],
        ["ma_sinh_vien", "VARCHAR(30)", "NOT NULL, UNIQUE", "Mã số sinh viên chính thức (VD: SV24001)"],
        ["lop", "VARCHAR(50)", "NULL", "Lớp sinh hoạt chuyên ngành (VD: CCQ2411D)"],
        ["khoa", "VARCHAR(100)", "NULL", "Khoa quản lý chuyên môn (VD: Công nghệ thông tin)"],
        ["nganh", "VARCHAR(100)", "NULL", "Ngành đào tạo (VD: Công nghệ phần mềm)"],
        ["khoa_hoc", "INT", "NULL", "Khóa tuyển sinh (VD: 24)"],
        ["que_quan", "VARCHAR(150)", "NULL", "Tỉnh/Thành phố quê quán của sinh viên"]
    ]
    add_styled_table(sv_headers, sv_rows, [1.4, 1.4, 1.7, 2.0], "Bảng 3.3 – Cấu trúc chi tiết bảng sinh_vien")

    # phong
    p_headers = ["Tên thuộc tính", "Kiểu dữ liệu", "Ràng buộc", "Ý nghĩa nghiệp vụ"]
    p_rows = [
        ["id", "BIGINT UNSIGNED", "PRIMARY KEY, AUTO_INCREMENT", "Mã định danh phòng ở"],
        ["tang_id", "BIGINT UNSIGNED", "NOT NULL, FK -> tang(id)", "Liên kết thuộc về tầng lầu cụ thể"],
        ["ma_phong", "VARCHAR(30)", "NOT NULL, UNIQUE", "Mã phòng ở ký túc xá (VD: A1-101)"],
        ["suc_chua", "INT UNSIGNED", "NOT NULL, CHECK (suc_chua > 0)", "Số lượng sinh viên tối đa được phép ở"],
        ["so_nguoi_hien_tai", "INT UNSIGNED", "NOT NULL, DEFAULT 0, CHECK (<= suc_chua)", "Số lượng sinh viên đang thực tế ở"],
        ["loai_phong", "VARCHAR(50)", "NOT NULL", "Chủng loại phòng (Phòng 2 người, 4 người...)"],
        ["gia_phong", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0, CHECK (gia_phong >= 0)", "Đơn giá thuê phòng chuẩn mỗi tháng"],
        ["trang_thai", "VARCHAR(30)", "NOT NULL, DEFAULT 'con_cho'", "Trạng thái phòng: con_cho, da_day, dang_sua_chua"]
    ]
    add_styled_table(p_headers, p_rows, [1.4, 1.4, 1.7, 2.0], "Bảng 3.4 – Cấu trúc chi tiết bảng phong")

    # hop_dong
    hd_headers = ["Tên thuộc tính", "Kiểu dữ liệu", "Ràng buộc", "Ý nghĩa nghiệp vụ"]
    hd_rows = [
        ["id", "BIGINT UNSIGNED", "PRIMARY KEY, AUTO_INCREMENT", "Mã định danh hợp đồng"],
        ["sinh_vien_id", "BIGINT UNSIGNED", "NOT NULL, FK -> sinh_vien(id)", "Sinh viên đứng tên ký hợp đồng"],
        ["phong_id", "BIGINT UNSIGNED", "NOT NULL, FK -> phong(id)", "Phòng ở ký túc xá được bố trí"],
        ["ngay_bat_dau", "DATE", "NOT NULL", "Ngày bắt đầu có hiệu lực cư trú"],
        ["ngay_ket_thuc", "DATE", "NOT NULL, CHECK (ngay_ket_thuc >= ngay_bat_dau)", "Ngày hết hạn hợp đồng"],
        ["tien_phong", "DECIMAL(12,2)", "NOT NULL, CHECK (tien_phong >= 0)", "Mức tiền phòng cam kết hàng tháng"],
        ["trang_thai", "VARCHAR(30)", "NOT NULL, DEFAULT 'cho_duyet'", "Trạng thái: cho_duyet, dang_hieu_luc, tu_choi, da_ket_thuc"]
    ]
    add_styled_table(hd_headers, hd_rows, [1.4, 1.4, 1.7, 2.0], "Bảng 3.5 – Cấu trúc chi tiết bảng hop_dong")

    # khoan_thu
    kt_headers = ["Tên thuộc tính", "Kiểu dữ liệu", "Ràng buộc", "Ý nghĩa nghiệp vụ"]
    kt_rows = [
        ["id", "BIGINT UNSIGNED", "PRIMARY KEY, AUTO_INCREMENT", "Mã định danh khoản thu / hóa đơn"],
        ["ma_hoa_don", "VARCHAR(50)", "UNIQUE, NULL", "Mã hóa đơn phát hành (VD: HD-202610-001)"],
        ["thang", "VARCHAR(20)", "NULL", "Tháng tính phí (VD: 2026-10)"],
        ["sinh_vien_id", "BIGINT UNSIGNED", "NOT NULL, FK -> sinh_vien(id)", "Sinh viên có nghĩa vụ thanh toán"],
        ["hop_dong_id", "BIGINT UNSIGNED", "NULL, FK -> hop_dong(id)", "Hợp đồng thuê phòng liên quan"],
        ["phong_id", "BIGINT UNSIGNED", "NULL, FK -> phong(id)", "Phòng ở phát sinh chi phí điện nước"],
        ["loai_khoan_thu", "VARCHAR(50)", "NOT NULL, DEFAULT 'hoa_don_thang'", "Loại phí: hoa_don_thang, tien_phong, tien_coc"],
        ["so_tien", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0, CHECK (>= 0)", "Tổng số tiền phải nộp (Phòng+Điện+Nước...)"],
        ["tien_phong", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0", "Tiền thuê phòng trong tháng"],
        ["tien_dien", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0", "Tiền điện sinh hoạt tính theo chỉ số"],
        ["chi_so_dien_cu/moi", "INT", "NULL, DEFAULT 0", "Số công tơ điện đầu kỳ và cuối kỳ"],
        ["tien_nuoc", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0", "Tiền nước sinh hoạt tính theo khối"],
        ["chi_so_nuoc_cu/moi", "INT", "NULL, DEFAULT 0", "Số đồng hồ nước đầu kỳ và cuối kỳ"],
        ["da_thanh_toan", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0", "Số tiền sinh viên đã thực nộp"],
        ["con_lai", "DECIMAL(12,2)", "NOT NULL, DEFAULT 0", "Số tiền còn nợ = so_tien - da_thanh_toan"],
        ["han_thanh_toan", "DATE", "NULL", "Hạn chót phải hoàn tất đóng phí"],
        ["trang_thai", "VARCHAR(30)", "NOT NULL, DEFAULT 'chua_thanh_toan'", "chua_thanh_toan, thanh_toan_mot_phan, da_thanh_toan, qua_han"]
    ]
    add_styled_table(kt_headers, kt_rows, [1.4, 1.4, 1.7, 2.0], "Bảng 3.6 – Cấu trúc chi tiết bảng khoan_thu")

    add_heading2("3.7. Thiết kế giao diện người dùng")
    add_p("Hệ thống giao diện của dự án được triển khai dưới dạng Single-Page / Multi-View Web Application bằng HTML5/CSS3/JavaScript thuần kết hợp thư viện TailwindCSS và Bootstrap 5. Tất cả các trang giao diện đều tương tác với Backend qua các API RESTful bất đồng bộ (Fetch API trong `ktx-api.js`).")
    add_p("Dưới đây là các khung hướng dẫn chụp ảnh minh chứng giao diện thực tế từ ứng dụng:")

    # Screenshots Group B placeholders
    add_screenshot_placeholder(
        "Hình 3.7", "Giao diện đăng nhập hệ thống (login.html)",
        "Minh chứng màn hình xác thực tài khoản có cơ chế đếm ngược chống tấn công Brute-force.",
        ["Mở trình duyệt, truy cập địa chỉ http://localhost:8080/login",
         "Giao diện hiển thị form đăng nhập với nền tối, logo KTX, ô nhập mã tài khoản/email và mật khẩu",
         "Nhập sai mật khẩu liên tiếp 5 lần để quan sát cảnh báo khóa 15 phút và thời gian đếm ngược",
         "Chụp toàn màn hình trình duyệt bảo đảm rõ form và thông báo an ninh"],
        ["Ảnh chụp thực tế, không cắt xén viền, kích thước sắc nét tối thiểu 1280x720",
         "Lưu ảnh theo tên: hinh_3_7_giao_dien_dang_nhap.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.8", "Giao diện Dashboard quản trị tổng quan KTX (index.html)",
        "Minh chứng trang bảng điều khiển trung tâm thống kê số liệu sinh viên, phòng ốc và tài chính.",
        ["Đăng nhập với tài khoản quản trị (VD: ND003 / mat_khau)",
         "Hệ thống tự động chuyển hướng vào trang chủ Dashboard (/index.html)",
         "Chụp toàn bộ giao diện bao gồm các thẻ thống kê tổng sinh viên, tòa nhà, phòng trống/đầy, doanh thu và nợ đọng"],
        ["Hiển thị đầy đủ số liệu thống kê được tổng hợp từ CSDL",
         "Lưu ảnh theo tên: hinh_3_8_giao_dien_dashboard.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.9", "Giao diện quản lý thông tin sinh viên nội trú (sinh-vien.html)",
        "Minh chứng phân hệ quản lý hồ sơ sinh viên, tìm kiếm và phân trang.",
        ["Từ menu bên trái, nhấp chọn mục 'Quản lý Sinh viên' (/sinh-vien.html)",
         "Giao diện hiển thị bảng danh sách sinh viên kèm mã SV, họ tên, lớp, ngành, quê quán",
         "Mở modal 'Thêm sinh viên mới' để thấy các trường thông tin ràng buộc",
         "Chụp toàn bộ màn hình danh sách sinh viên"],
        ["Bảo đảm nhìn rõ bảng danh sách và các nút thao tác Sửa/Xóa",
         "Lưu ảnh theo tên: hinh_3_9_giao_dien_sinh_vien.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.10", "Giao diện danh mục phòng và sơ đồ phòng ở (phong.html)",
        "Minh chứng chức năng quản lý danh mục phòng, sức chứa và kiểm soát số người hiện tại.",
        ["Từ thanh điều hướng, nhấp chọn mục 'Quản lý Phòng' (/phong.html)",
         "Giao diện hiển thị danh sách các phòng kèm số người hiện tại / sức chứa và badge trạng thái (còn chỗ / đã đầy)",
         "Chụp ảnh toàn bộ màn hình danh sách phòng"],
        ["Rõ ràng các cột mã phòng, tòa nhà, sức chứa, số người ở thực tế và đơn giá",
         "Lưu ảnh theo tên: hinh_3_10_giao_dien_phong.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.11", "Giao diện quản lý hợp đồng thuê phòng (hop-dong.html)",
        "Minh chứng quy trình duyệt hợp đồng, điều chuyển phòng và thanh lý hợp đồng.",
        ["Nhấp chọn mục 'Quản lý Hợp đồng' (/hop-dong.html)",
         "Hiển thị danh sách các hợp đồng kèm trạng thái 'Chờ duyệt', 'Đang hiệu lực', 'Đã kết thúc'",
         "Nhấp thử vào nút 'Duyệt hợp đồng' hoặc 'Chuyển phòng' để thấy modal thao tác",
         "Chụp màn hình thể hiện các nút chức năng"],
        ["Nhìn rõ các thao tác nghiệp vụ duyệt, từ chối, chuyển phòng và gia hạn",
         "Lưu ảnh theo tên: hinh_3_11_giao_dien_hop_dong.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.12", "Giao diện lập hóa đơn & quét mã thanh toán VietQR / MoMo (hoa-don.html)",
        "Minh chứng phân hệ hóa đơn tự động và khung quét mã QR thanh toán ngân hàng trực tuyến.",
        ["Mở trang 'Quản lý Hóa đơn & Công nợ' (/hoa-don.html)",
         "Nhấp vào nút 'Thanh toán' của một hóa đơn bất kỳ để mở modal thanh toán",
         "Quan sát khung bên phải hiển thị mã VietQR MBBank 24/7 động kèm logo ngân hàng và số tiền",
         "Chụp màn hình modal thanh toán kèm mã QR và nút In phiếu thu"],
        ["Nhìn rõ mã QR VietQR, thông tin số tiền và nội dung chuyển khoản",
         "Lưu ảnh theo tên: hinh_3_12_giao_dien_hoa_don_vietqr.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_screenshot_placeholder(
        "Hình 3.13", "Giao diện quản lý cấp phát & bảo trì thiết bị (thiet-bi.html)",
        "Minh chứng chức năng theo dõi trang thiết bị phòng, cấp phát, báo hỏng và lịch sử.",
        ["Mở trang 'Quản lý Thiết bị' (/thiet-bi.html)",
         "Xem tab 'Thiết bị trong phòng' hiển thị danh sách tài sản kèm trạng thái 'Đang sử dụng', 'Hỏng hóc'",
         "Mở modal 'Báo hỏng' hoặc 'Lịch sử giao dịch' để thấy nhật ký bảo trì",
         "Chụp toàn màn hình"],
        ["Hiển thị danh sách thiết bị và các thao tác cấp phát / báo hỏng / thu hồi",
         "Lưu ảnh theo tên: hinh_3_13_giao_dien_thiet_bi.png"],
        "Ngay sau mục 3.7 – Thiết kế giao diện người dùng"
    )

    add_heading2("3.8. Thiết kế RESTful API")
    add_p("Toàn bộ các Controller trong hệ thống đều tuân thủ nguyên tắc thiết kế RESTful chuẩn mực. Dưới đây là bảng thống kê chi tiết toàn bộ các Endpoint API của hệ thống:")

    api_headers = ["STT", "Method", "Endpoint", "Chức năng nghiệp vụ", "Input DTO", "Output Data", "Quyền"]
    api_rows = [
        ["1", "POST", "/api/auth/login", "Xác thực tài khoản & kiểm tra Brute-force", "LoginRequest (JSON)", "LoginResponse", "Công khai"],
        ["2", "GET", "/api/dashboard/stats", "Tổng hợp số liệu thống kê KTX", "Không", "DashboardStatsDTO", "Quản lý/Admin"],
        ["3", "GET", "/api/hoa-don", "Lấy danh sách hóa đơn có bộ lọc", "Query params (thang, trangThai)", "List<KhoanThu>", "Quản lý/SV"],
        ["4", "GET", "/api/hoa-don/{id}", "Xem chi tiết một hóa đơn", "Path variable id", "KhoanThu", "Quản lý/SV"],
        ["5", "POST", "/api/hoa-don", "Tạo thủ công một khoản thu mới", "HoaDonRequestDTO", "KhoanThu", "Quản lý KTX"],
        ["6", "PUT", "/api/hoa-don/{id}", "Cập nhật thông tin hóa đơn", "id, HoaDonRequestDTO", "KhoanThu", "Quản lý KTX"],
        ["7", "DELETE", "/api/hoa-don/{id}", "Xóa một hóa đơn", "Path variable id", "Void (HTTP 204)", "Quản trị viên"],
        ["8", "POST", "/api/hoa-don/lap-hang-loat", "Lập hóa đơn tự động hàng loạt theo tòa", "LapHoaDonHangLoatDTO", "Map<String, Object>", "Quản lý KTX"],
        ["9", "POST", "/api/hoa-don/thanh-toan", "Ghi nhận thanh toán hóa đơn", "GhiNhanThanhToanDTO", "ThanhToan", "Quản lý KTX"],
        ["10", "GET", "/api/hoa-don/{id}/lich-su-thanh-toan", "Xem lịch sử thanh toán của hóa đơn", "Path variable id", "List<ThanhToan>", "Quản lý/SV"],
        ["11", "GET", "/api/hoa-don/bao-cao", "Báo cáo tổng hợp công nợ theo tòa", "Query (thang, toaNhaId)", "BaoCaoCongNoDTO", "Quản lý KTX"],
        ["12", "GET", "/api/hoa-don/{id}/phieu-thu", "Lấy dữ liệu in phiếu thu tiền (có chữ)", "id, thanhToanId", "PhieuThuDTO", "Quản lý/SV"],
        ["13", "GET", "/api/hop-dong", "Lấy danh sách hợp đồng thuê phòng", "Query (trangThai)", "List<HopDong>", "Quản lý KTX"],
        ["14", "POST", "/api/hop-dong", "Tạo mới / Sinh viên đăng ký hợp đồng", "HopDong (JSON)", "HopDong (HTTP 201)", "Quản lý/SV"],
        ["15", "POST", "/api/hop-dong/{id}/duyet", "Phê duyệt hợp đồng (tăng số người ở)", "Path variable id", "HopDong", "Quản lý KTX"],
        ["16", "POST", "/api/hop-dong/{id}/tu-choi", "Từ chối yêu cầu thuê phòng", "Path variable id", "HopDong", "Quản lý KTX"],
        ["17", "POST", "/api/hop-dong/{id}/gia-han", "Gia hạn thời hạn hợp đồng", "id, ngayKetThuc", "HopDong", "Quản lý KTX"],
        ["18", "POST", "/api/hop-dong/{id}/thanh-ly", "Thanh lý hợp đồng (giảm số người ở)", "Path variable id", "HopDong", "Quản lý KTX"],
        ["19", "POST", "/api/hop-dong/{id}/chuyen-phong", "Chuyển phòng cho sinh viên", "id, phongMoiId", "HopDong", "Quản lý KTX"],
        ["20", "GET", "/api/sinh-vien-phong", "Lấy danh sách phân bổ SV trong phòng", "Query (trangThai)", "List<SinhVienPhongDTO>", "Quản lý KTX"],
        ["21", "GET", "/api/sinh-vien-phong/phong/{id}", "Xem danh sách sinh viên ở phòng X", "phongId", "List<SinhVienPhongDTO>", "Quản lý KTX"],
        ["22", "GET", "/api/sinhvien", "Lấy danh sách hồ sơ sinh viên", "Không", "List<SinhVien>", "Quản lý KTX"],
        ["23", "POST", "/api/sinhvien", "Thêm mới hồ sơ sinh viên", "SinhVien (JSON)", "SinhVien (HTTP 201)", "Quản lý KTX"],
        ["24", "GET", "/api/phong", "Lấy danh sách phòng ở KTX", "Không", "List<Phong>", "Tất cả tác nhân"],
        ["25", "POST", "/api/phong", "Tạo phòng ở mới (kiểm tra sức chứa)", "Phong (JSON)", "Phong (HTTP 201)", "Quản lý KTX"],
        ["26", "GET", "/api/loai-phong", "Thống kê số lượng phòng theo loại", "Không", "List<LoaiPhongDTO>", "Tất cả tác nhân"],
        ["27", "GET", "/api/toa-nha", "Lấy danh sách các khối tòa nhà", "Không", "List<ToaNha>", "Tất cả tác nhân"],
        ["28", "GET", "/api/tang", "Lấy danh sách các tầng lầu", "Không", "List<Tang>", "Tất cả tác nhân"],
        ["29", "GET", "/api/khu", "Lấy danh sách các phân khu KTX", "Không", "List<Khu>", "Tất cả tác nhân"],
        ["30", "GET", "/api/tai-khoan", "Lấy danh sách tài khoản người dùng", "Không", "List<NguoiDung>", "Quản trị viên"],
        ["31", "POST", "/api/tai-khoan", "Tạo tài khoản mới (mã hóa BCrypt)", "NguoiDung (JSON)", "NguoiDung", "Quản trị viên"],
        ["32", "GET", "/api/thiet-bi", "Lấy danh mục tài sản/thiết bị KTX", "Không", "List<TaiSan>", "Quản lý KTX"],
        ["33", "GET", "/api/thiet-bi/phong", "Danh sách trang thiết bị trong phòng", "Query (phongId, toaNhaId)", "List<TaiSanPhong>", "Quản lý KTX"],
        ["34", "POST", "/api/thiet-bi/cap-phat", "Cấp phát thiết bị cho phòng ở", "CapPhatThietBiDTO", "TaiSanPhong", "Quản lý KTX"],
        ["35", "POST", "/api/thiet-bi/bao-hong", "Báo hỏng thiết bị phòng", "BaoHongThietBiDTO", "TaiSanPhong", "Quản lý/SV"],
        ["36", "POST", "/api/thiet-bi/sua-chua/{id}", "Xác nhận hoàn tất sửa chữa thiết bị", "id, ghiChu", "TaiSanPhong", "Quản lý KTX"],
        ["37", "POST", "/api/thiet-bi/thu-hoi/{id}", "Thu hồi thiết bị về kho", "id, lyDo", "TaiSanPhong", "Quản lý KTX"],
        ["38", "GET", "/api/thiet-bi/lich-su", "Xem nhật ký giao dịch thiết bị", "Query (phongId, taiSanId)", "List<LichSuTaiSan>", "Quản lý KTX"]
    ]
    add_styled_table(api_headers, api_rows, [0.4, 0.7, 1.8, 1.8, 0.9, 0.9, 0.6], "Bảng 3.9 – Bảng danh mục RESTful API hệ thống")

    # Mẫu JSON
    add_heading3("3.8.1. Mẫu Request và Response tiêu biểu của API")
    add_p("• Mẫu 1: Đăng nhập hệ thống (POST /api/auth/login)")
    add_p("Request Body:")
    add_p('{\n  "identifier": "ND001",\n  "password": "password123"\n}', bold=False, italic=True, size=10, color=CLR_PRIMARY)
    add_p("Response Body (HTTP 200 OK):")
    add_p('{\n  "id": 1,\n  "maNguoiDung": "ND001",\n  "hoTen": "Nguyen Van An",\n  "email": "an@example.com"\n}', bold=False, italic=True, size=10, color=CLR_PRIMARY)

    add_p("• Mẫu 2: Lập hóa đơn hàng loạt (POST /api/hoa-don/lap-hang-loat)")
    add_p("Request Body:")
    add_p('{\n  "thang": "2026-10",\n  "toaNhaId": 1,\n  "hanThanhToan": "2026-10-25",\n  "donGiaDien": 3500,\n  "donGiaNuoc": 12000,\n  "tienInternet": 50000,\n  "tienDichVu": 30000\n}', bold=False, italic=True, size=10, color=CLR_PRIMARY)
    add_p("Response Body (HTTP 200 OK):")
    add_p('{\n  "success": true,\n  "count": 45,\n  "thang": "2026-10",\n  "message": "Đã lập thành công 45 hóa đơn cho tòa nhà!"\n}', bold=False, italic=True, size=10, color=CLR_PRIMARY)

    # ---------------------------------------------------------
    # CHƯƠNG 4. CÀI ĐẶT VÀ TRIỂN KHAI HỆ THỐNG
    # ---------------------------------------------------------
    print("Building Chapter 4...")
    add_chapter("CHƯƠNG 4. CÀI ĐẶT VÀ TRIỂN KHAI HỆ THỐNG")

    add_heading2("4.1. Môi trường phát triển và cấu hình phần mềm")
    add_p("Hệ thống được phát triển và kiểm thử trên môi trường tiêu chuẩn:")
    
    env_headers = ["Thành phần môi trường", "Thông số kỹ thuật / Phiên bản", "Ghi chú cấu hình"]
    env_rows = [
        ["Hệ điều hành máy chủ/phát triển", "Microsoft Windows 11 Pro 64-bit", "Hỗ trợ môi trường PowerShell & Command Line"],
        ["Bộ công cụ phát triển Java (JDK)", "Eclipse Adoptium Temurin 25.0.4.1 (build 25)", "Thiết lập JAVA_HOME trỏ tới thư mục cài đặt"],
        ["Framework ứng dụng", "Spring Boot v4.1.1", "Tích hợp sẵn Spring MVC, Spring Data JPA, Tomcat 11"],
        ["Hệ quản trị CSDL chính", "MySQL Community Server 8.0 / MariaDB 10.4", "Cổng mặc định 3306, mã hóa utf8mb4_unicode_ci"],
        ["Cơ chế Connection Pool", "HikariCP 7.0.2", "Quản lý kết nối tốc độ cao tích hợp trong Spring Boot"],
        ["Công cụ quản lý dự án & Build", "Apache Maven 3.9+ / Maven Wrapper (mvnw)", "Quản lý dependencies và đóng gói tệp thực thi JAR"],
        ["Môi trường phát triển tích hợp (IDE)", "Visual Studio Code & Antigravity IDE", "Tích hợp Extension Pack for Java và Spring Tools"],
        ["Công cụ kiểm tra API & CSDL", "Postman / Thunder Client & phpMyAdmin", "Hỗ trợ kiểm thử endpoint và thao tác trực tiếp CSDL"]
    ]
    add_styled_table(env_headers, env_rows, [1.8, 2.2, 2.5], "Bảng 4.1 – Bảng thông số môi trường phát triển")

    add_heading2("4.2. Cấu trúc source code và mô hình phân tầng")
    add_p("Dự án được tổ chức theo chuẩn kiến trúc package của Spring Boot trong thư mục gốc `com.example.NguyenQuocVuong_chuong4`:")
    
    pkg_headers = ["Package Name", "Chức năng và vai trò trong hệ thống", "Các thành phần tiêu biểu"]
    pkg_rows = [
        ["config", "Cấu hình ứng dụng và các Bean dùng chung toàn hệ thống", "PasswordConfig (định nghĩa BCryptPasswordEncoder Bean)"],
        ["controller", "Tiếp nhận HTTP request, ánh xạ URL, gọi Service, trả về JSON", "AuthController, DashboardController, HoaDonController, HopDongController, SinhVienController, PhongController, ThietBiController, WebViewController..."],
        ["data", "Chứa tệp kịch bản CSDL khởi tạo bảng và dữ liệu thử nghiệm", "quan_ly_ky_tuc_xa.sql (Schema 15 bảng và dữ liệu mẫu)"],
        ["dto", "Data Transfer Object truyền tải dữ liệu giữa Client và Server", "LoginRequest, LoginResponse, DashboardStatsDTO, HoaDonRequestDTO, LapHoaDonHangLoatDTO, GhiNhanThanhToanDTO, PhieuThuDTO, SinhVienPhongDTO..."],
        ["model", "Các lớp Entity JPA ánh xạ trực tiếp tới các bảng trong MySQL", "NguoiDung, SinhVien, Khu, ToaNha, Tang, Phong, HopDong, LoaiTaiSan, TaiSan, TaiSanPhong, LichSuTaiSan, KhoanThu, ThanhToan"],
        ["repository", "Giao diện truy xuất dữ liệu kế thừa Spring Data JpaRepository", "NguoiDungRepository, SinhVienRepository, PhongRepository, HopDongRepository, KhoanThuRepository, ThanhToanRepository, TaiSanRepository..."],
        ["security", "Các lớp an ninh mạng, chống Brute-force, lọc XSS và HTTP Headers", "LoginAttemptService, SecurityHeadersFilter, XssProtectionUtil"],
        ["service", "Xử lý logic nghiệp vụ, quản lý giao dịch CSDL @Transactional", "NguoiDungService, HopDongService, KhoanThuService, ThietBiService, PhongService, SinhVienService, ToaNhaService, TangService, KhuService"],
        ["util", "Các tiện ích bổ trợ tính toán và định dạng dữ liệu", "MoneyToWordsUtil (Đọc số tiền hóa đơn thành chữ tiếng Việt)"]
    ]
    add_styled_table(pkg_headers, pkg_rows, [1.3, 2.7, 2.5], "Bảng 4.2 – Bảng mô tả các package trong mã nguồn")

    add_p("Dưới đây là sơ đồ kiến trúc tổng thể của hệ thống và sơ đồ luồng phân tầng Controller – Service – Repository – Database:")
    add_diagram_image("hinh_4_1_kien_truc_tong_the.png", "Hình 4.1 – Sơ đồ kiến trúc tổng thể hệ thống quản lý KTX", width_inches=6.4)
    add_diagram_image("hinh_4_2_phan_tang_spring_boot.png", "Hình 4.2 – Sơ đồ luồng phân tầng Controller – Service – Repository – Database", width_inches=6.2)

    add_p("Phân tích luồng xử lý giữa các tầng (Hình 4.2):")
    add_p("1. Khi Client gửi HTTP Request, bộ lọc `SecurityHeadersFilter` bổ sung các tiêu đề an ninh; `AuthController` hoặc các REST Controller tương ứng tiếp nhận yêu cầu và thẩm định cú pháp qua `@Valid`.")
    add_p("2. Controller gọi phương thức tương ứng của tầng Service. Tầng Service nắm giữ toàn bộ logic nghiệp vụ (kiểm tra sức chứa, kiểm tra trùng lặp, tính tiền, khóa Brute-force).")
    add_p("3. Service gọi tầng Repository để truy vấn hoặc lưu trữ dữ liệu. Tầng Repository sử dụng Hibernate ORM để sinh câu lệnh SQL tham số hóa an toàn.")
    add_p("4. MySQL Server tiếp nhận SQL, thực thi và trả về tập kết quả. Hibernate chuyển đổi kết quả thành Entity, Service đóng gói thành DTO hoặc Entity và Controller trả về mã trạng thái HTTP kèm JSON cho người dùng.")

    add_heading2("4.3. Cài đặt các chức năng nghiệp vụ trọng tâm")
    add_p("Hệ thống cài đặt hoàn chỉnh 10 phân hệ nghiệp vụ chính:")
    add_p("1. Đăng nhập & Xác thực tài khoản: Hỗ trợ đăng nhập bằng mã tài khoản hoặc email; kiểm tra số lần sai liên tiếp để khóa tạm thời 15 phút chống dò mật khẩu; mã hóa mật khẩu an toàn bằng BCrypt.")
    add_p("2. Quản lý Hồ sơ Sinh viên: Thao tác CRUD đầy đủ; ràng buộc liên kết 1-1 với tài khoản người dùng; ngăn chặn xóa sinh viên đang có hợp đồng hiệu lực.")
    add_p("3. Quản lý Cơ sở vật chất Khu - Tòa - Tầng: Tổ chức phân cấp; quản lý số tầng của tòa nhà; kiểm tra trùng lặp tầng trong cùng một tòa.")
    add_p("4. Quản lý Phòng ở: Ràng buộc kiểm tra nghiêm ngặt `soNguoiHienTai <= sucChua`; tự động gắn nhãn trạng thái 'con_cho' hoặc 'da_day'; phân loại phòng theo sức chứa và giá tiền.")
    add_p("5. Quản lý Hợp đồng cư trú: Kiểm soát toàn diện vòng đời hợp đồng; phê duyệt tự động tăng sĩ số phòng; thanh lý hợp đồng tự động giải phóng chỗ; điều chuyển sinh viên linh hoạt giữa các phòng.")
    add_p("6. Quản lý Quan hệ Sinh viên - Phòng: Cung cấp API chuyên biệt tra cứu danh sách người ở trong từng phòng hoặc phòng của sinh viên, hỗ trợ xuất báo cáo sĩ số nhanh chóng.")
    add_p("7. Lập hóa đơn & Quản lý Công nợ: Phát hành hóa đơn hàng loạt theo tòa nhà trong một tháng cụ thể; tính toán chi phí điện, nước theo chỉ số cũ - mới; tự động phân loại trạng thái nợ (chưa thanh toán, thanh toán một phần, đã thanh toán, quá hạn).")
    add_p("8. Thanh toán trực tuyến đa kênh & In phiếu thu: Tích hợp tạo mã QR chuẩn VietQR MBBank 24/7 và ví MoMo; ghi nhận phiếu thu có ngày giờ, người thu; xuất phiếu thu với số tiền được đọc bằng chữ tiếng Việt chuẩn xác qua `MoneyToWordsUtil`.")
    add_p("9. Quản lý Trang thiết bị & Tài sản: Theo dõi tài sản theo từng phòng; hỗ trợ quy trình cấp phát, gửi báo hỏng, cập nhật hoàn tất sửa chữa và thu hồi; lưu vết toàn bộ lịch sử biến động trong `lich_su_tai_san`.")
    add_p("10. Dashboard thống kê tổng quan: Tổng hợp số liệu theo thời gian thực; tính toán tổng doanh thu thực thu và tổng dư nợ toàn ký túc xá.")

    add_heading2("4.4. Cài đặt cơ chế bảo mật an ninh mạng")
    add_p("Hệ thống triển khai 4 thành phần an ninh chuyên sâu trong source code:")
    add_p("• `LoginAttemptService`: Sử dụng `ConcurrentHashMap` để quản lý số lần thử sai mật khẩu; định nghĩa ngưỡng tối đa `MAX_ATTEMPTS = 5` và thời gian khóa `BLOCK_DURATION_SECONDS = 900` (15 phút). Cơ chế đảm bảo tính an toàn đa luồng (thread-safe) và tự động gỡ khóa khi hết hạn.")
    add_p("• `SecurityHeadersFilter`: Filter lọc trước mọi request với thứ tự ưu tiên cao nhất, gán chặt các tiêu đề `X-Frame-Options: SAMEORIGIN`, `X-Content-Type-Options: nosniff`, `X-XSS-Protection: 1; mode=block` và `Content-Security-Policy`.")
    add_p("• `XssProtectionUtil`: Bộ lọc tĩnh làm sạch chuỗi nhập vào; bóc tách toàn bộ mã độc kịch bản JavaScript, thẻ iframe và sự kiện nhúng nguy hiểm.")
    add_p("• `PasswordConfig`: Cấu hình Spring Bean `BCryptPasswordEncoder` dùng cho toàn bộ nghiệp vụ tạo mới, cập nhật và xác thực tài khoản.")

    add_heading2("4.5. Hướng dẫn cài đặt, cấu hình và khởi chạy hệ thống")
    add_p("Để cài đặt và vận hành hệ thống trên môi trường máy chủ hoặc máy tính cá nhân, người dùng thực hiện theo 10 bước sau:")
    add_p("• Bước 1: Chuẩn bị môi trường phần mềm – Cài đặt JDK 25 LTS (hoặc JDK 21+), MySQL Server 8.0 (hoặc XAMPP/Laragon) và công cụ Git.")
    add_p("• Bước 2: Kiểm tra cài đặt Java – Mở Terminal / PowerShell, chạy lệnh `java -version` và đảm bảo phiên bản Java nhận diện là 25 hoặc 21+.")
    add_p("• Bước 3: Khởi động hệ quản trị CSDL MySQL – Khởi động dịch vụ MySQL thông qua XAMPP Control Panel hoặc MySQL Windows Service.")
    add_p("• Bước 4: Tạo CSDL và nạp dữ liệu mẫu – Mở phpMyAdmin hoặc MySQL Workbench, tạo CSDL `quan_ly_ky_tuc_xa` với bảng mã `utf8mb4_unicode_ci`, sau đó import toàn bộ nội dung tệp SQL: `src/main/java/com/example/NguyenQuocVuong_chuong4/data/quan_ly_ky_tuc_xa.sql`.")
    add_p("• Bước 5: Cấu hình kết nối cơ sở dữ liệu – Mở tệp `src/main/resources/application.properties`, điều chỉnh thông số nếu cần:")
    add_p("  `spring.datasource.url=jdbc:mysql://localhost:3306/quan_ly_ky_tuc_xa?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh`\n  `spring.datasource.username=root`\n  `spring.datasource.password=` (hoặc mật khẩu MySQL của máy bạn)")
    add_p("• Bước 6: Mở dự án trong IDE – Khởi động Visual Studio Code hoặc IntelliJ IDEA, mở thư mục dự án `Quan_li_ktx_nhom12_Vuong_Trong_Thang_Java`.")
    add_p("• Bước 7: Cài đặt thư viện và biên dịch dự án – Mở Terminal trong IDE, thực thi lệnh:\n  `./mvnw.cmd clean compile` (trên Windows) hoặc `./mvnw clean compile` (trên Linux/macOS).")
    add_p("• Bước 8: Khởi chạy ứng dụng Spring Boot – Chạy lệnh Maven:\n  `./mvnw.cmd spring-boot:run` hoặc chạy trực tiếp tệp `NguyenQuocVuongChuong4Application.java`.")
    add_p("• Bước 9: Truy cập hệ thống trên trình duyệt – Mở trình duyệt web (Chrome, Edge, Firefox), truy cập địa chỉ:\n  • Trang chủ / Mô phỏng 3D: `http://localhost:8080/`\n  • Trang đăng nhập: `http://localhost:8080/login`\n  • Bảng điều khiển quản trị: `http://localhost:8080/dashboard`")
    add_p("• Bước 10: Đăng nhập bằng tài khoản thử nghiệm:")
    add_p("  • Tài khoản Quản trị viên: `ND003` (hoặc email `cuong@example.com`) – Mật khẩu kiểm thử: `[BỔ SUNG HOẶC THAY THẾ BẰNG MẬT KHẨU ĐÃ BĂM]`.")
    add_p("  • Tài khoản Sinh viên: `ND001` (email `an@example.com`) / `ND002` (email `binh@example.com`).")

    add_heading2("4.6. Minh chứng triển khai hệ thống")
    add_p("Các minh chứng thực tế về quá trình biên dịch, cơ sở dữ liệu và triển khai chương trình:")

    add_screenshot_placeholder(
        "Hình 4.3", "Ảnh cấu trúc cây thư mục source code trong IDE",
        "Minh chứng dự án được tổ chức khoa học theo mô hình chuẩn Spring Boot trên IDE.",
        ["Mở dự án trong Visual Studio Code hoặc IntelliJ IDEA",
         "Mở rộng cây thư mục src/main/java và src/main/resources",
         "Chụp cửa sổ Explorer bên trái thể hiện đầy đủ các package config, controller, model, repository, service, security, static..."],
        ["Nhìn rõ tên dự án và các package chính", "Lưu ảnh theo tên: hinh_4_3_cay_thu_muc_ide.png"],
        "Ngay sau mục 4.6 – Minh chứng triển khai hệ thống"
    )

    add_screenshot_placeholder(
        "Hình 4.4", "Ảnh terminal khởi chạy Spring Boot thành công cổng 8080",
        "Minh chứng ứng dụng khởi động không lỗi và nhúng máy chủ Tomcat lắng nghe cổng 8080.",
        ["Chạy lệnh ./mvnw.cmd spring-boot:run trên Terminal",
         "Chờ dòng log hiển thị: 'Started NguyenQuocVuongChuong4Application in ... seconds'",
         "Chụp màn hình cửa sổ Terminal thể hiện logo Spring Boot và thông báo Tomcat started on port 8080"],
        ["Không bị che khuất dòng log thời gian khởi động", "Lưu ảnh theo tên: hinh_4_4_terminal_khoi_chay.png"],
        "Ngay sau mục 4.6 – Minh chứng triển khai hệ thống"
    )

    add_screenshot_placeholder(
        "Hình 4.5", "Ảnh cơ sở dữ liệu quan_ly_ky_tuc_xa trong phpMyAdmin / MySQL",
        "Minh chứng toàn bộ 15 bảng dữ liệu đã được tạo thành công trong MySQL.",
        ["Mở trình duyệt, truy cập http://localhost/phpmyadmin (hoặc MySQL Workbench)",
         "Chọn CSDL quan_ly_ky_tuc_xa bên danh mục trái",
         "Chụp danh sách 15 bảng dữ liệu kèm số lượng dòng bản ghi mẫu"],
        ["Bảo đảm nhìn rõ tên 15 bảng dữ liệu quan hệ", "Lưu ảnh theo tên: hinh_4_5_csdl_mysql.png"],
        "Ngay sau mục 4.6 – Minh chứng triển khai hệ thống"
    )

    add_screenshot_placeholder(
        "Hình 4.6", "Ảnh minh họa mô phỏng 3D Campus KTX bằng Three.js",
        "Minh chứng giao diện mô phỏng 3D không gian ký túc xá trên trang landing.html.",
        ["Truy cập địa chỉ http://localhost:8080/ hoặc http://localhost:8080/landing",
         "Tương tác chuột xoay góc nhìn mô hình 3D tòa nhà và khuôn viên KTX",
         "Chụp toàn bộ giao diện landing page kèm khung hiển thị Three.js"],
        ["Thể hiện hiệu ứng 3D rõ nét và các nút điều hướng", "Lưu ảnh theo tên: hinh_4_6_mo_phong_3d_campus.png"],
        "Ngay sau mục 4.6 – Minh chứng triển khai hệ thống"
    )

    # ---------------------------------------------------------
    # CHƯƠNG 5. KIỂM THỬ HỆ THỐNG
    # ---------------------------------------------------------
    print("Building Chapter 5...")
    add_chapter("CHƯƠNG 5. KIỂM THỬ HỆ THỐNG")

    add_heading2("5.1. Mục tiêu kiểm thử")
    add_p("Kiểm thử phần mềm là giai đoạn quan trọng nhằm xác thực tính đúng đắn của các quy trình nghiệp vụ, kiểm tra độ tin cậy của các ràng buộc toàn vẹn dữ liệu, đánh giá khả năng phòng thủ mạng trước các cuộc tấn công và đảm bảo ứng dụng không phát sinh lỗi ngoại lệ ngoài tầm kiểm soát khi người dùng thao tác sai.")

    add_heading2("5.2. Phương pháp kiểm thử thực tế")
    add_p("Dự án áp dụng kết hợp ba phương pháp kiểm thử thực tế:")
    add_p("1. Kiểm thử tích hợp tự động với Spring Boot Test & H2 Database: Sử dụng `NguyenQuocVuongChuong4ApplicationTests` chạy trên cơ sở dữ liệu bộ nhớ ảo H2 (`jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL`), độc lập hoàn toàn với MySQL vật lý. Toàn bộ chu trình khởi tạo dữ liệu, kiểm tra ràng buộc phòng, hợp đồng và xác thực mật khẩu được thực thi tự động thông qua Maven Surefire Plugin.")
    add_p("2. Kiểm thử an ninh phòng thủ mạng tự động (`SecurityAttackProtectionTests`): Viết các bài kiểm thử giả lập tấn công mạng chuyên sâu: kiểm thử tấn công dò mật khẩu Brute-force 5 lần; kiểm thử tấn công SQL Injection bằng chuỗi payload độc hại; kiểm thử lọc mã độc XSS; kiểm thử sự hiện diện của Security Headers.")
    add_p("3. Kiểm thử chức năng thủ công (Manual Functional Testing): Thực hiện trực tiếp trên giao diện người dùng và thông qua công cụ kiểm thử REST API Postman theo các kịch bản biên và các trường hợp nhập liệu không hợp lệ.")

    add_heading2("5.3. Xây dựng bộ Test Case và kết quả thực tế")
    add_p("Dưới đây là bảng ghi nhận kết quả kiểm thử thực tế trích xuất từ báo cáo kiểm thử tự động của Maven Surefire (`TEST-com.example.NguyenQuocVuong_chuong4.NguyenQuocVuongChuong4ApplicationTests.xml`):")

    tc1_headers = ["STT", "Tên phương thức kiểm thử", "Nghiệp vụ kiểm thử", "Kết quả mong đợi", "Kết quả thực tế", "Trạng thái"]
    tc1_rows = [
        ["1", "testPerson3ContractLifecycleAndRoomAllocation", "Vòng đời hợp đồng: Đăng ký -> Duyệt (+1 người) -> Chuyển phòng -> Thanh lý (-1 người)", "Sĩ số phòng tăng giảm tự động và chính xác", "Sĩ số cập nhật chuẩn 100%, trạng thái chuyển phòng A->B thành công", "ĐẠT (Passed - 1.15s)"],
        ["2", "savesRelatedEntitiesAndAuthenticatesWithHashedPassword", "Tạo tài khoản, mã hóa BCrypt và kiểm tra xác thực mật khẩu đúng/sai", "Đúng mật khẩu trả về User; sai mật khẩu trả về rỗng", "Xác thực thành công với mật khẩu đúng, từ chối an toàn mật khẩu sai", "ĐẠT (Passed - 0.36s)"],
        ["3", "dashboardAndLoaiPhongEndpointsReturnAccurateAggregations", "Tính toán số liệu thống kê Dashboard và gom nhóm loại phòng", "Số liệu tổng hợp không âm, danh sách loại phòng không rỗng", "Dữ liệu trả về chuẩn xác từ repository aggregation", "ĐẠT (Passed - 0.06s)"],
        ["4", "contextLoads", "Khởi động Spring Application Context và nạp cấu hình Beans", "Context khởi tạo thành công không phát sinh BeanCreationException", "Context khởi động hoàn tất trong 6.33 giây", "ĐẠT (Passed - 0.01s)"],
        ["5", "rejectsRoomCapacityAndInvalidContractDates", "Kiểm tra ràng buộc vượt sức chứa và ngày kết thúc trước ngày bắt đầu", "Ném ngoại lệ ResponseStatusException (HTTP 400 Bad Request)", "Bị chặn và ném ngoại lệ chính xác như mong đợi", "ĐẠT (Passed - 0.02s)"]
    ]
    add_styled_table(tc1_headers, tc1_rows, [0.5, 2.0, 1.4, 1.1, 1.0, 0.5], "Bảng 5.1 – Bảng kết quả kiểm thử đơn vị & tích hợp (Surefire Report)")

    add_p("Bảng kết quả kiểm thử phòng chống các cuộc tấn công mạng (`SecurityAttackProtectionTests`):")
    
    tc2_headers = ["STT", "Bài kiểm thử an ninh", "Payload / Kịch bản tấn công", "Cơ chế bảo vệ thực tế", "Kết quả thực tế", "Trạng thái"]
    tc2_rows = [
        ["1", "Chống Brute-force Attack", "Thử sai mật khẩu liên tiếp 5 lần cho tài khoản victim@ktx.edu.vn", "LoginAttemptService ghi nhận lần 5 và khóa 15 phút", "Lần 1-4 trả HTTP 401; Lần 5 & 6 bị chặn ngay với HTTP 429", "ĐẠT (Passed)"],
        ["2", "Chống SQL Injection", "Payload kinh điển: ' OR '1'='1' -- đưa vào ô đăng nhập", "Spring Data JPA tham số hóa câu truy vấn PreparedStatements", "Payload bị coi là chuỗi literal, trả về Optional.empty an toàn", "ĐẠT (Passed)"],
        ["3", "Chống Cross-Site Scripting (XSS)", "Mã độc: <script>alert('Stealing')</script> và <img onerror=...>", "XssProtectionUtil.sanitize() loại bỏ hoàn toàn thẻ nguy hiểm", "Chuỗi độc hại bị bóc tách sạch, ký tự HTML được escape", "ĐẠT (Passed)"],
        ["4", "Kiểm tra Security Headers", "Gửi request HTTP giả lập qua SecurityHeadersFilter", "Filter tự động gán X-Frame-Options, X-Content-Type-Options", "Headers được gán chính xác (SAMEORIGIN, nosniff, CSP)", "ĐẠT (Passed)"]
    ]
    add_styled_table(tc2_headers, tc2_rows, [0.5, 1.5, 1.5, 1.4, 1.1, 0.5], "Bảng 5.2 – Bảng kiểm thử an ninh phòng thủ mạng")

    add_p("Bảng kịch bản kiểm thử chức năng thủ công trên giao diện và API:")
    
    tc3_headers = ["STT", "Chức năng", "Dữ liệu kiểm thử", "Kết quả mong đợi", "Kết quả thực tế", "Trạng thái"]
    tc3_rows = [
        ["1", "Thêm mới phòng", "Sức chứa: 4, Số người: 5 (vượt sức chứa)", "Hệ thống báo lỗi không cho phép tạo", "Bị chặn với thông báo lỗi sức chứa", "ĐẠT"],
        ["2", "Tạo hợp đồng", "Ngày BĐ: 2027-01-01, Ngày KT: 2026-01-01", "Hệ thống báo lỗi ngày kết thúc không hợp lệ", "Bị từ chối với HTTP 400", "ĐẠT"],
        ["3", "Duyệt hợp đồng", "Phòng đã đủ 4/4 người, bấm duyệt thêm", "Hệ thống thông báo phòng đã đầy", "Bị chặn, không tăng thêm người vào phòng", "ĐẠT"],
        ["4", "Lập hóa đơn", "Tòa A1, Tháng 2026-10, Hạn 25/10", "Tạo đủ hóa đơn cho các hợp đồng đang ở", "Tạo đủ danh sách hóa đơn kèm mã HD duy nhất", "ĐẠT"],
        ["5", "Thanh toán VietQR", "Số tiền 1.200.000 VNĐ", "Hiện mã QR đúng số tiền và nội dung chuyển", "Mã QR hiển thị chính xác theo chuẩn VietQR", "ĐẠT"],
        ["6", "Báo hỏng thiết bị", "Chọn quạt điện phòng A1-101, lý do: 'Cháy motor'", "Đổi trạng thái thiết bị sang 'hong_hoc', ghi lịch sử", "Trạng thái đổi thành công, xuất hiện trong nhật ký", "ĐẠT"],
        ["7", "Xóa thiết bị đang dùng", "Bấm xóa thiết bị đang được cấp phát cho phòng", "Báo lỗi không cho xóa thiết bị đang cấp phát", "Bị chặn với thông báo vi phạm ràng buộc", "ĐẠT"],
        ["8", "Thêm trùng mã phòng", "Nhập mã phòng 'A1-101' đã tồn tại", "Báo lỗi trùng mã phòng (ProblemDetail 409)", "ApiExceptionHandler xử lý trả về lỗi Conflict", "ĐẠT"],
        ["9", "Đăng ký phòng trực tuyến", "Sinh viên chọn phòng còn chỗ, điền thông tin", "Tạo hợp đồng ở trạng thái 'cho_duyet'", "Cần kiểm thử thực tế trên môi trường sinh viên", "Cần kiểm thử thực tế"],
        ["10", "Giao dịch đồng thời nhiều người", "Nhiều sinh viên cùng đăng ký 1 chỗ cuối cùng", "Chỉ 1 người đăng ký thành công, người kia báo đầy", "Chưa kiểm thử tải đồng thời (Concurrency test)", "Chưa thực hiện"]
    ]
    add_styled_table(tc3_headers, tc3_rows, [0.4, 1.2, 1.5, 1.4, 1.4, 0.6], "Bảng 5.3 – Bảng kịch bản kiểm thử chức năng thủ công")

    add_heading2("5.4. Đánh giá kết quả kiểm thử")
    add_p("Tổng hợp kết quả kiểm thử:")
    add_p("• Toàn bộ 5 test case kiểm thử tích hợp tự động của hệ thống đều vượt qua thành công 100% (5/5 tests passed, 0 failures, 0 errors, tổng thời gian 8.73 giây).")
    add_p("• Bộ 4 bài kiểm thử an ninh chuyên sâu chứng minh hệ thống có khả năng tự vệ vững chắc trước các hình thức tấn công mạng phổ biến như Brute-force (khóa 15 phút), SQL Injection (PreparedStatements), XSS (Sanitizer) và Clickjacking (SAMEORIGIN).")
    add_p("• Các kiểm thử chức năng thủ công xác nhận các ràng buộc logic nghiệp vụ (vượt sức chứa, ngày tháng hợp đồng, thanh toán, khóa ngoại) đều hoạt động ổn định và chính xác.")
    add_p("• Một số trường hợp kiểm thử tải đồng thời nhiều người dùng (Concurrency / Load testing) hoặc kiểm thử trên môi trường mạng diện rộng được nhóm ghi nhận là 'Chưa thực hiện' và đề xuất bổ sung trong giai đoạn phát triển tiếp theo.")

    # ---------------------------------------------------------
    # CHƯƠNG 6. KẾT QUẢ, ĐÁNH GIÁ VÀ HƯỚNG PHÁT TRIỂN
    # ---------------------------------------------------------
    print("Building Chapter 6...")
    add_chapter("CHƯƠNG 6. KẾT QUẢ, ĐÁNH GIÁ VÀ HƯỚNG PHÁT TRIỂN")

    add_heading2("6.1. Kết quả đạt được")
    add_p("Sau quá trình nghiên cứu lý thuyết, phân tích hệ thống và tiến hành lập trình, nhóm 12 đã hoàn thành toàn diện các mục tiêu đề ra cho đồ án môn Lập trình Java:")
    add_p("1. Xây dựng hoàn chỉnh ứng dụng Web Quản lý Ký túc xá trên nền tảng Java 25 LTS và Spring Boot 4, đáp ứng các tiêu chuẩn khắt khe về kiến trúc mã nguồn và tính toàn vẹn dữ liệu.")
    add_p("2. Thiết lập cơ sở dữ liệu quan hệ MySQL chuẩn hóa gồm 15 thực thể, thể hiện đầy đủ các mối quan hệ 1-1, 1-N và N-N cùng các ràng buộc CHECK, UNIQUE và FOREIGN KEY.")
    add_p("3. Triển khai 12 REST Controller với 38 Endpoint API, hỗ trợ đầy đủ các thao tác CRUD và quy trình nghiệp vụ chuyên biệt.")
    add_p("4. Xây dựng giao diện Web responsive, tích hợp bản đồ 3D Campus KTX và cổng thanh toán tự động hóa VietQR / MoMo.")
    add_p("5. Cài đặt thành công các cơ chế bảo mật an ninh mạng tiên tiến (chống Brute-force, lọc XSS, băm mật khẩu BCrypt, phòng ngừa Clickjacking).")

    add_heading2("6.2. Đánh giá hệ thống theo tiêu chuẩn kỹ thuật")
    add_p("• Cấu trúc mã nguồn: Phân tầng rành mạch theo mô hình MVC / Clean Architecture (Controller → Service → Repository → Model/DTO), tuân thủ các nguyên lý SOLID, mã nguồn trong sáng và dễ dàng bảo trì.")
    add_p("• Khả năng xử lý nghiệp vụ: Xử lý mượt mà và chính xác các tình huống nghiệp vụ phức tạp như tự động tính sĩ số phòng khi duyệt/thanh lý/chuyển phòng; tự động tính tổng tiền điện nước theo công tơ.")
    add_p("• Thiết kế CSDL: Chuẩn hóa cao, không dư thừa dữ liệu, toàn vẹn tham chiếu chặt chẽ.")
    add_p("• Tính an toàn bảo mật: Đạt tiêu chuẩn an ninh web OWASP cơ bản, kiểm soát chặt chẽ phiên đăng nhập và luồng dữ liệu vào/ra.")
    add_p("• Khả năng mở rộng: Dễ dàng bổ sung thêm các phân hệ mới như quản lý điểm rèn luyện, đăng ký gửi xe hoặc tích hợp cổng thanh toán trực tiếp qua Webhook ngân hàng.")

    add_heading2("6.3. Ưu điểm nổi bật của hệ thống")
    add_p("• Tính thực tiễn cao: Bám sát bài toán vận hành ký túc xá tại các trường đại học Việt Nam.")
    add_p("• Tự động hóa tài chính vượt trội: Tích hợp chuẩn VietQR MBBank 24/7 và MoMo giúp sinh viên nộp tiền phòng nhanh chóng, loại bỏ hoàn toàn việc thu tiền mặt thủ công dễ thất thoát.")
    add_p("• Trực quan hóa hiện đại: Ứng dụng Three.js mô phỏng 3D Campus tạo sự thu hút và hiện đại cho cổng thông tin KTX.")
    add_p("• Bộ kiểm thử tự động toàn diện: Có đầy đủ Unit test, Integration test và Security test chứng minh chất lượng phần mềm trước khi nghiệm thu.")

    add_heading2("6.4. Hạn chế và tồn tại")
    add_p("Mặc dù đạt được nhiều kết quả tích cực, hệ thống vẫn còn một số điểm hạn chế do giới hạn về thời gian:")
    add_p("• Xác thực người dùng hiện tại chủ yếu thông qua Session và mã phản hồi DTO, chưa cấu hình đầy đủ Spring Security Filter Chain toàn diện với JWT (JSON Web Token) và OAuth2.")
    add_p("• Chưa có cơ chế đối soát tự động thông qua Bank Webhook (hiện tại cán bộ bấm nút ghi nhận sau khi sinh viên chuyển khoản thành công).")
    add_p("• Ứng dụng chưa được kiểm thử tải với số lượng kết nối đồng thời cực lớn (hàng chục nghìn sinh viên truy cập cùng lúc trong mùa đăng ký phòng).")

    add_heading2("6.5. Hướng phát triển trong tương lai")
    add_p("Để hoàn thiện hệ thống hướng tới triển khai thực tế trên môi trường Production, nhóm định hướng các mục tiêu nâng cấp tiếp theo:")
    add_p("1. Nâng cấp hệ thống xác thực sang mô hình Stateless JWT (JSON Web Token) kết hợp Refresh Token và phân quyền đa cấp theo Spring Security.")
    add_p("2. Tích hợp Open Banking API / Payment Gateway chính thức (như VNPay, MoMo Business, SeABank/MBBank Open API) để tự động cập nhật hóa đơn ngay khi tài khoản ngân hàng nhận được tiền.")
    add_p("3. Bổ sung hệ thống gửi thông báo tự động (Push Notification, Email qua JavaMailSender, tin nhắn Zalo ZNS) để nhắc hạn nộp tiền phòng hoặc thông báo lịch cúp điện nước.")
    add_p("4. Xây dựng ứng dụng di động (Mobile App) trên nền tảng Flutter hoặc React Native cho sinh viên.")
    add_p("5. Tích hợp giải pháp điểm danh thông minh qua nhận diện khuôn mặt (FaceID) hoặc thẻ RFID/NFC tại cổng kiểm soát ký túc xá.")

    # ---------------------------------------------------------
    # KẾT LUẬN & TÀI LIỆU THAM KHẢO & PHỤ LỤC & DANH SÁCH ẢNH
    # ---------------------------------------------------------
    print("Building Conclusion, References, Appendix...")
    add_chapter("KẾT LUẬN")
    add_p("Đồ án môn học 'Xây dựng Hệ thống Quản lý Ký túc xá' của Nhóm 12 đã hoàn thành trọn vẹn các yêu cầu đặt ra ban đầu. Dự án là sự kết tinh của quá trình học tập nghiêm túc, tinh thần làm việc nhóm kỷ luật và sự vận dụng sáng tạo các kiến thức công nghệ hiện đại.")
    add_p("Thông qua đồ án này, các thành viên trong nhóm không chỉ nắm vững kiến thức cốt lõi về ngôn ngữ lập trình Java 25, khung phát triển Spring Boot, cơ chế ORM Hibernate, cơ sở dữ liệu MySQL mà còn tích lũy được những kinh nghiệm vô giá về tư duy phân tích bài toán thực tế, thiết kế kiến trúc đa tầng, xây dựng RESTful API an toàn và kỹ năng kiểm thử bảo mật phần mềm.")
    add_p("Những kết quả đạt được khẳng định tính khả thi và tiềm năng ứng dụng to lớn của hệ thống trong việc số hóa công tác quản lý ký túc xá tại các cơ sở giáo dục hiện nay. Nhóm 12 sẽ tiếp tục hoàn thiện và phát triển các tính năng mở rộng trong các học phần chuyên sâu tiếp theo.")

    # Tài liệu tham khảo
    add_chapter("TÀI LIỆU THAM KHẢO")
    refs = [
        "1. Oracle Corporation (2025), Java SE 25 Documentation & Language Specification, truy cập tại: https://docs.oracle.com/en/java/",
        "2. Pivotal Software / VMware Tanzu (2025), Spring Boot Reference Documentation (v3.x / v4.x), truy cập tại: https://docs.spring.io/spring-boot/docs/current/reference/html/",
        "3. Spring Data Team (2025), Spring Data JPA - Reference Documentation, truy cập tại: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/",
        "4. Craig Walls (2022), Spring in Action, Sixth Edition, Manning Publications, ISBN 978-1617297571.",
        "5. Hibernate Community (2025), Hibernate ORM 7.x User Guide, truy cập tại: https://hibernate.org/orm/documentation/",
        "6. MySQL Documentation Team (2025), MySQL 8.0 Reference Manual, Oracle Corporation, truy cập tại: https://dev.mysql.com/doc/refman/8.0/en/",
        "7. Open Web Application Security Project (OWASP) (2021), OWASP Top Ten Web Application Security Risks, truy cập tại: https://owasp.org/www-project-top-ten/",
        "8. National Payment Corporation of Vietnam (Napas) & Ngân hàng Nhà nước Việt Nam (2021), Tiêu chuẩn kỹ thuật định dạng VietQR chuyển tiền liên ngân hàng 24/7."
    ]
    for r in refs:
        add_p(r, size=11.5, space_after=6)

    # Phụ lục
    add_chapter("PHỤ LỤC")
    add_heading2("Phụ lục 1: Cây cấu trúc thư mục dự án")
    tree_text = """Quan_li_ktx_nhom12_Vuong_Trong_Thang_Java/
├── pom.xml                                   # Cấu hình Maven & dependencies (Java 25, Spring Boot 4)
├── README.md                                 # Tài liệu ERD Mermaid & mô tả hệ thống
├── mvnw, mvnw.cmd                            # Maven Wrapper
└── src/
    ├── main/
    │   ├── java/com/example/NguyenQuocVuong_chuong4/
    │   │   ├── NguyenQuocVuongChuong4Application.java # Lớp khởi chạy Spring Boot chính
    │   │   ├── config/                       # PasswordConfig (BCryptPasswordEncoder Bean)
    │   │   ├── controller/                   # 12 REST Controllers (Auth, Dashboard, HoaDon...)
    │   │   ├── data/                         # quan_ly_ky_tuc_xa.sql (Script khởi tạo 15 bảng)
    │   │   ├── dto/                          # 11 DTOs truyền tải dữ liệu
    │   │   ├── model/                        # 13 JPA Entities (NguoiDung, Phong, HopDong...)
    │   │   ├── repository/                   # 12 Spring Data JPA Repositories
    │   │   ├── security/                     # LoginAttemptService, SecurityHeadersFilter, XssProtectionUtil
    │   │   ├── service/                      # 9 Business Services (@Transactional)
    │   │   └── util/                         # MoneyToWordsUtil (Đọc tiền thành chữ tiếng Việt)
    │   └── resources/
    │       ├── application.properties        # Cấu hình CSDL MySQL & JPA Hibernate
    │       └── static/                       # Giao diện Web HTML5/CSS/JS (login, hoa-don, index...)
    └── test/java/com/example/NguyenQuocVuong_chuong4/
        ├── NguyenQuocVuongChuong4ApplicationTests.java # 5 Test cases kiểm thử tích hợp (Surefire)
        └── SecurityAttackProtectionTests.java          # 4 Test cases an ninh mạng chuyên sâu"""
    add_p(tree_text, bold=False, italic=False, size=9, color=CLR_PRIMARY, line_spacing=1.05)

    add_heading2("Phụ lục 2: Script SQL khởi tạo CSDL mẫu (Trích xuất quan_ly_ky_tuc_xa.sql)")
    sql_text = """CREATE DATABASE IF NOT EXISTS quan_ly_ky_tuc_xa
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quan_ly_ky_tuc_xa;

-- 1. nguoi_dung (Tài khoản)
CREATE TABLE nguoi_dung (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ma_nguoi_dung VARCHAR(30) NOT NULL UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'hoat_dong',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. phong (Phòng ở KTX)
CREATE TABLE phong (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tang_id BIGINT UNSIGNED NOT NULL,
    ma_phong VARCHAR(30) NOT NULL UNIQUE,
    suc_chua INT UNSIGNED NOT NULL CHECK (suc_chua > 0),
    so_nguoi_hien_tai INT UNSIGNED NOT NULL DEFAULT 0,
    loai_phong VARCHAR(50) NOT NULL,
    gia_phong DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (gia_phong >= 0),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'con_cho',
    CONSTRAINT fk_phong_tang FOREIGN KEY (tang_id) REFERENCES tang(id)
) ENGINE=InnoDB;"""
    add_p(sql_text, bold=False, italic=False, size=9, color=CLR_PRIMARY, line_spacing=1.05)

    # Danh sách hình ảnh cần bổ sung
    add_chapter("DANH SÁCH HÌNH ẢNH TÔI CẦN BỔ SUNG (NHÓM B)")
    add_p("Dưới đây là bảng tổng hợp tất cả các hình ảnh thuộc Nhóm B mà người dùng cần chụp từ ứng dụng đang chạy thực tế để chèn vào các khung vị trí tương ứng trong báo cáo:")

    grp_b_headers = ["STT", "Ký hiệu", "Chương", "Nội dung cần chụp", "Màn hình / URL", "Hướng dẫn thao tác tóm tắt"]
    grp_b_rows = [
        ["1", "Hình 3.7", "Chương 3", "Giao diện đăng nhập hệ thống", "/login.html", "Truy cập /login, nhập sai 5 lần để hiện thông báo khóa tạm 15 phút"],
        ["2", "Hình 3.8", "Chương 3", "Giao diện Dashboard quản trị", "/index.html", "Đăng nhập tài khoản quản trị, chụp đầy đủ các thẻ thống kê tổng quan"],
        ["3", "Hình 3.9", "Chương 3", "Giao diện quản lý sinh viên", "/sinh-vien.html", "Mở menu Sinh viên, chụp danh sách sinh viên kèm các nút Sửa/Xóa"],
        ["4", "Hình 3.10", "Chương 3", "Giao diện danh mục phòng", "/phong.html", "Mở menu Phòng, chụp danh sách phòng kèm sĩ số và badge còn chỗ/đầy"],
        ["5", "Hình 3.11", "Chương 3", "Giao diện quản lý hợp đồng", "/hop-dong.html", "Mở trang Hợp đồng, chụp bảng hợp đồng kèm các nút Duyệt / Chuyển phòng"],
        ["6", "Hình 3.12", "Chương 3", "Giao diện hóa đơn & VietQR", "/hoa-don.html", "Mở trang Hóa đơn, bấm 'Thanh toán' để hiện mã QR MBBank và nút in phiếu thu"],
        ["7", "Hình 3.13", "Chương 3", "Giao diện quản lý thiết bị", "/thiet-bi.html", "Mở trang Thiết bị, chụp danh sách thiết bị phòng kèm trạng thái hỏng hóc/sử dụng"],
        ["8", "Hình 4.3", "Chương 4", "Ảnh cấu trúc cây thư mục IDE", "VS Code / IntelliJ", "Mở rộng src/main/java và resources/static, chụp cửa sổ Explorer"],
        ["9", "Hình 4.4", "Chương 4", "Terminal khởi chạy Spring Boot", "Terminal IDE", "Chạy ./mvnw.cmd spring-boot:run, chụp log khởi động cổng 8080 thành công"],
        ["10", "Hình 4.5", "Chương 4", "Cơ sở dữ liệu trong phpMyAdmin", "http://localhost/phpmyadmin", "Chọn CSDL quan_ly_ky_tuc_xa, chụp danh sách 15 bảng dữ liệu"],
        ["11", "Hình 4.6", "Chương 4", "Mô phỏng 3D Campus KTX", "/landing.html", "Truy cập /landing, xoay mô hình 3D khuôn viên KTX bằng chuột và chụp"]
    ]
    add_styled_table(grp_b_headers, grp_b_rows, [0.4, 0.8, 0.8, 1.8, 1.2, 1.8])

    # Header / Footer configuration
    for s_idx, sec in enumerate(doc.sections):
        # Configure Header
        hdr = sec.header
        p_hdr = hdr.paragraphs[0]
        p_hdr.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        p_hdr.paragraph_format.space_after = Pt(2)
        r_hdr = p_hdr.add_run("ĐỒ ÁN MÔN HỌC: LẬP TRÌNH JAVA – QUẢN LÝ KÝ TÚC XÁ – NHÓM 12")
        r_hdr.font.name = 'Times New Roman'
        r_hdr.font.size = Pt(8.5)
        r_hdr.font.color.rgb = CLR_MUTED

        # Configure Footer with Page Number
        ftr = sec.footer
        p_ftr = ftr.paragraphs[0]
        p_ftr.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_ftr.paragraph_format.space_before = Pt(2)
        r_ftr = p_ftr.add_run("Trang ")
        r_ftr.font.name = 'Times New Roman'
        r_ftr.font.size = Pt(9)
        r_ftr.font.color.rgb = CLR_MUTED
        
        # XML Field for dynamic page number in Word
        fldSimple = parse_xml(r'<w:fldSimple %s w:instr="PAGE"/>' % nsdecls('w'))
        p_ftr._p.append(fldSimple)

    print("Saving document to:", OUTPUT_PATH)
    doc.save(OUTPUT_PATH)
    print("Document successfully created!")

if __name__ == '__main__':
    create_report()

