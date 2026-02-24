#!/usr/bin/env python3
"""
Script para convertir PREGUNTERO_TECNICO.md a PDF
"""

import os
import sys

try:
    from reportlab.lib.pagesizes import letter, A4
    from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
    from reportlab.lib.units import inch
    from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle
    from reportlab.lib import colors
    from reportlab.pdfgen import canvas
except ImportError:
    print("Instalando reportlab...")
    os.system("pip install -q reportlab")
    from reportlab.lib.pagesizes import letter, A4
    from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
    from reportlab.lib.units import inch
    from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
    from reportlab.lib import colors

def markdown_to_pdf(md_file, pdf_file):
    """Convierte Markdown a PDF de forma simple"""
    
    # Leer archivo markdown
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Crear documento PDF
    doc = SimpleDocTemplate(pdf_file, pagesize=letter, topMargin=0.75*inch, bottomMargin=0.75*inch)
    styles = getSampleStyleSheet()
    
    # Estilos personalizados
    title_style = ParagraphStyle(
        'CustomTitle',
        parent=styles['Heading1'],
        fontSize=24,
        textColor=colors.HexColor('#1f4788'),
        spaceAfter=30,
        alignment=1  # Centro
    )
    
    heading_style = ParagraphStyle(
        'CustomHeading',
        parent=styles['Heading2'],
        fontSize=14,
        textColor=colors.HexColor('#2e5c8a'),
        spaceAfter=12,
        spaceBefore=12
    )
    
    question_style = ParagraphStyle(
        'Question',
        parent=styles['Normal'],
        fontSize=11,
        textColor=colors.HexColor('#1a1a1a'),
        fontName='Helvetica-Bold',
        spaceAfter=6
    )
    
    answer_style = ParagraphStyle(
        'Answer',
        parent=styles['Normal'],
        fontSize=10,
        textColor=colors.HexColor('#333333'),
        leftIndent=20,
        spaceAfter=12,
        borderPadding=5
    )
    
    # Procesar contenido
    story = []
    lines = content.split('\n')
    
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        
        # Título principal
        if line.startswith('# ') and not line.startswith('## '):
            title = line.replace('# ', '').strip()
            story.append(Paragraph(title, title_style))
            story.append(Spacer(1, 0.2*inch))
        
        # Subtítulos (categorías)
        elif line.startswith('## '):
            subtitle = line.replace('## ', '').strip()
            story.append(PageBreak())
            story.append(Paragraph(subtitle, heading_style))
        
        # Preguntas
        elif line.startswith('### '):
            question = line.replace('### ', '').replace(')', '', 1).strip()
            story.append(Paragraph(f"<b>{question}</b>", question_style))
        
        # Respuestas
        elif line.startswith('**Respuesta:**'):
            # Obtener texto de respuesta (puede ser multilinea)
            response = line.replace('**Respuesta:**', '').strip()
            i += 1
            while i < len(lines) and lines[i].strip() and not lines[i].startswith('---'):
                response += ' ' + lines[i].strip()
                i += 1
            i -= 1
            story.append(Paragraph(response, answer_style))
            story.append(Spacer(1, 0.15*inch))
        
        i += 1
    
    # Generar PDF
    doc.build(story)
    print(f"✅ PDF generado: {pdf_file}")

if __name__ == '__main__':
    md_file = 'PREGUNTERO_TECNICO.md'
    pdf_file = 'PREGUNTERO_TECNICO.pdf'
    
    if not os.path.exists(md_file):
        print(f"❌ Archivo no encontrado: {md_file}")
        sys.exit(1)
    
    try:
        markdown_to_pdf(md_file, pdf_file)
    except Exception as e:
        print(f"❌ Error: {e}")
        sys.exit(1)
