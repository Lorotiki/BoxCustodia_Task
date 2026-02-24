# 🎯 GUÍA: Cómo Convertir a PDF

## Opción 1: Usar Google Chrome/Chromium (⭐ Recomendado - Sin software)

1. Abre el archivo `PREGUNTERO_TECNICO.html` en tu navegador
2. Presiona `Ctrl+P` (Windows/Linux) o `Cmd+P` (Mac)
3. En "Destino", cambia a "Guardar como PDF"
4. Haz clic en "Guardar"

**Resultado:** PDF bonito con colores y formato.

---

## Opción 2: Usando wkhtmltopdf (Linux/Mac)

### Instalar wkhtmltopdf:
```bash
# Ubuntu/Debian
sudo apt-get install wkhtmltopdf

# macOS (con Homebrew)
brew install wkhtmltopdf

# Fedora
sudo dnf install wkhtmltopdf
```

### Convertir:
```bash
wkhtmltopdf PREGUNTERO_TECNICO.html PREGUNTERO_TECNICO.pdf
```

---

## Opción 3: Usando Pandoc

### Instalar:
```bash
# Ubuntu/Debian
sudo apt-get install pandoc texlive-latex-base texlive-fonts-recommended

# macOS
brew install pandoc
```

### Convertir:
```bash
pandoc PREGUNTERO_TECNICO.md -o PREGUNTERO_TECNICO.pdf --pdf-engine=xelatex
```

---

## Opción 4: Online (Sin instalar nada)

Sube el HTML a servicios online:
- **CloudConvert**: https://cloudconvert.com/
- **PDF Pro**: https://pdfpro.co/
- **Zamzar**: https://www.zamzar.com/

---

## ✅ Verificación

Una vez tengas el PDF, verifica:
- ✓ Todas 12 categorías presentes
- ✓ 120 preguntas totales
- ✓ Formato legible
- ✓ Colores aplicados

**¡Listo para tu entrevista! 🚀**
