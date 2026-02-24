#!/usr/bin/env python3
"""
Convertir Markdown a HTML y luego a PDF usando wkhtmltopdf
"""

import re

def markdown_to_html(md_file, html_file):
    """Convierte Markdown a HTML"""
    
    with open(md_file, 'r', encoding='utf-8') as f:
        md_content = f.read()
    
    # Convertir markdown a HTML simple
    html = """<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Preguntero Técnico - TaskFlow API</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 960px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f9f9f9;
        }
        
        h1 {
            color: #1f4788;
            text-align: center;
            border-bottom: 3px solid #2e5c8a;
            padding-bottom: 15px;
            margin-bottom: 30px;
        }
        
        h2 {
            color: #2e5c8a;
            border-left: 5px solid #1f4788;
            padding-left: 15px;
            page-break-before: always;
            margin-top: 40px;
        }
        
        h3 {
            color: #1a1a1a;
            margin-top: 20px;
            margin-bottom: 8px;
        }
        
        .respuesta {
            background-color: #f0f4f8;
            border-left: 4px solid #2e5c8a;
            padding: 12px 15px;
            margin: 10px 0 20px 0;
            border-radius: 4px;
        }
        
        .respuesta-label {
            font-weight: bold;
            color: #2e5c8a;
            margin-bottom: 5px;
        }
        
        .respuesta-texto {
            color: #444;
            font-size: 14px;
        }
        
        hr {
            border: none;
            height: 1px;
            background-color: #ddd;
            margin: 40px 0;
        }
        
        .indice {
            background-color: #f0f4f8;
            padding: 20px;
            border-radius: 5px;
            margin: 20px 0;
        }
        
        .indice ul {
            list-style-type: none;
            padding-left: 0;
        }
        
        .indice li {
            padding: 5px 0;
        }
        
        .indice a {
            color: #2e5c8a;
            text-decoration: none;
        }
        
        .indice a:hover {
            text-decoration: underline;
        }
        
        @media print {
            body { background-color: white; }
            h2 { page-break-before: always; }
        }
    </style>
</head>
<body>
"""
    
    # Procesar líneas
    lines = md_content.split('\n')
    
    for i, line in enumerate(lines):
        if line.startswith('# ') and not line.startswith('## '):
            # Título principal
            title = line.replace('# ', '').strip()
            html += f"<h1>{title}</h1>\n"
        
        elif line.startswith('## '):
            # Subtítulo
            subtitle = line.replace('## ', '').strip()
            html += f"<h2>{subtitle}</h2>\n"
        
        elif line.startswith('### '):
            # Pregunta
            question = line.replace('### ', '').strip()
            html += f"<h3>{question}</h3>\n"
        
        elif line.startswith('**Respuesta:**'):
            # Respuesta
            response = line.replace('**Respuesta:**', '').strip()
            html += f'<div class="respuesta"><div class="respuesta-label">Respuesta:</div><div class="respuesta-texto">{response}</div></div>\n'
        
        elif line.startswith('---'):
            html += "<hr>\n"
        
        elif line.strip() == '':
            continue
        
        elif line.startswith('- '):
            # Lista
            item = line.replace('- ', '').strip()
            html += f"<li>{item}</li>\n"
        
        else:
            # Párrafo normal
            if line.strip() and not line.startswith('---'):
                html += f"<p>{line.strip()}</p>\n"
    
    html += """
</body>
</html>
"""
    
    with open(html_file, 'w', encoding='utf-8') as f:
        f.write(html)
    
    print(f"✅ HTML generado: {html_file}")
    return html_file

if __name__ == '__main__':
    md_file = 'PREGUNTERO_TECNICO.md'
    html_file = 'PREGUNTERO_TECNICO.html'
    
    try:
        markdown_to_html(md_file, html_file)
        print("\n📝 Puedes abrir el HTML en navegador o convertirlo a PDF con:")
        print("   wkhtmltopdf PREGUNTERO_TECNICO.html PREGUNTERO_TECNICO.pdf")
    except Exception as e:
        print(f"❌ Error: {e}")
