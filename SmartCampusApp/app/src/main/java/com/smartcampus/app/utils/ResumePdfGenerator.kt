package com.smartcampus.app.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream

object ResumePdfGenerator {

    fun generatePdf(
        context: Context,
        templateId: String,
        fullName: String,
        email: String,
        phone: String,
        summary: String,
        education: String,
        skills: String,
        experience: String,
        projects: String
    ): File? {
        return try {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, "${fullName.replace(" ", "_")}_Resume.pdf")
            val writer = PdfWriter(FileOutputStream(file))
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A4)
            document.setMargins(36f, 36f, 36f, 36f)

            when (templateId) {
                "modern" -> buildModernTemplate(document, fullName, email, phone, summary, education, skills, experience, projects)
                "creative" -> buildCreativeTemplate(document, fullName, email, phone, summary, education, skills, experience, projects)
                else -> buildClassicTemplate(document, fullName, email, phone, summary, education, skills, experience, projects)
            }

            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun buildClassicTemplate(
        doc: Document, name: String, email: String, phone: String, summary: String,
        education: String, skills: String, experience: String, projects: String
    ) {
        // Header
        doc.add(Paragraph(name.uppercase()).setBold().setFontSize(16f).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0f))
        
        // Contact info with blue link style for email
        val contactPara = Paragraph().setFontSize(10f).setTextAlignment(TextAlignment.CENTER).setMarginBottom(4f)
        if (phone.isNotBlank()) contactPara.add("$phone | ")
        
        val emailText = com.itextpdf.layout.element.Text(email).setFontColor(DeviceRgb(41, 128, 185))
        contactPara.add(emailText)
        contactPara.add(" | India") // Filler for location to match style
        doc.add(contactPara)

        // Thick Horizontal Line
        val thickLineTable = Table(1).useAllAvailableWidth()
        thickLineTable.addCell(Cell()
            .setBorderTop(SolidBorder(ColorConstants.BLACK, 2.5f))
            .setBorderBottom(Border.NO_BORDER)
            .setBorderLeft(Border.NO_BORDER)
            .setBorderRight(Border.NO_BORDER))
        doc.add(thickLineTable.setMarginTop(2f).setMarginBottom(6f))

        // Summary without header
        if (summary.isNotBlank()) {
            doc.add(Paragraph(summary).setFontSize(10f).setMarginBottom(6f))
        }

        addClassicSection(doc, "Core Competencies", skills)
        addClassicSection(doc, "Professional Experience", experience)
        addClassicSection(doc, "Projects", projects)
        addClassicSection(doc, "Education", education)
    }

    private fun addClassicSection(doc: Document, title: String, content: String) {
        if (content.isBlank()) return
        
        // Centered Section Header with thin lines above and below
        val sectionTable = Table(1).useAllAvailableWidth().setMarginTop(2f).setMarginBottom(4f)
        val cell = Cell()
            .setBorderTop(SolidBorder(ColorConstants.BLACK, 1f))
            .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f))
            .setBorderLeft(Border.NO_BORDER)
            .setBorderRight(Border.NO_BORDER)
            .add(Paragraph(title).setBold().setFontSize(11f).setTextAlignment(TextAlignment.CENTER))
            .setPaddingTop(1f)
            .setPaddingBottom(1f)
            
        sectionTable.addCell(cell)
        doc.add(sectionTable)
        
        // Content
        doc.add(Paragraph(content).setFontSize(10f).setMarginBottom(6f))
    }

    private fun buildModernTemplate(
        doc: Document, name: String, email: String, phone: String, summary: String,
        education: String, skills: String, experience: String, projects: String
    ) {
        val primaryColor = DeviceRgb(41, 128, 185) // Blueish
        
        // Header
        val headerTable = Table(1).useAllAvailableWidth().setBackgroundColor(primaryColor)
        val nameCell = Cell().add(Paragraph(name).setBold().setFontSize(26f).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER).setPadding(10f)
        val contactCell = Cell().add(Paragraph("$email  |  $phone").setFontSize(12f).setFontColor(ColorConstants.WHITE)).setBorder(Border.NO_BORDER).setPadding(10f).setPaddingTop(0f)
        headerTable.addCell(nameCell)
        headerTable.addCell(contactCell)
        doc.add(headerTable.setMarginBottom(20f))

        addModernSection(doc, "PROFESSIONAL SUMMARY", summary, primaryColor)
        addModernSection(doc, "EDUCATION", education, primaryColor)
        addModernSection(doc, "TECH SKILLS", skills, primaryColor)
        addModernSection(doc, "WORK EXPERIENCE", experience, primaryColor)
        addModernSection(doc, "KEY PROJECTS", projects, primaryColor)
    }

    private fun addModernSection(doc: Document, title: String, content: String, color: DeviceRgb) {
        if (content.isBlank()) return
        doc.add(Paragraph(title).setBold().setFontSize(14f).setFontColor(color).setMarginTop(10f))
        doc.add(Paragraph(content).setFontSize(11f).setMarginTop(2f).setMarginBottom(8f))
    }

    private fun buildCreativeTemplate(
        doc: Document, name: String, email: String, phone: String, summary: String,
        education: String, skills: String, experience: String, projects: String
    ) {
        val darkColor = DeviceRgb(44, 62, 80)
        
        doc.add(Paragraph(name).setBold().setFontSize(28f).setFontColor(darkColor))
        doc.add(Paragraph("$email  •  $phone").setFontSize(12f).setFontColor(ColorConstants.GRAY).setMarginBottom(20f))

        val splitTable = Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f))).useAllAvailableWidth()
        
        // Left Column
        val leftColumn = Cell().setBorder(Border.NO_BORDER).setPaddingRight(15f)
        addCreativeSection(leftColumn, "CONTACT", "$email\n$phone")
        addCreativeSection(leftColumn, "SKILLS", skills.replace(", ", "\n"))
        addCreativeSection(leftColumn, "EDUCATION", education)
        
        // Right Column
        val rightColumn = Cell().setBorder(Border.NO_BORDER).setBorderLeft(SolidBorder(ColorConstants.LIGHT_GRAY, 1f)).setPaddingLeft(15f)
        addCreativeSection(rightColumn, "PROFILE", summary)
        addCreativeSection(rightColumn, "EXPERIENCE", experience)
        addCreativeSection(rightColumn, "PROJECTS", projects)

        splitTable.addCell(leftColumn)
        splitTable.addCell(rightColumn)
        doc.add(splitTable)
    }

    private fun addCreativeSection(cell: Cell, title: String, content: String) {
        if (content.isBlank()) return
        cell.add(Paragraph(title).setBold().setFontSize(12f).setFontColor(DeviceRgb(44, 62, 80)).setMarginTop(10f))
        cell.add(Paragraph(content).setFontSize(10f).setMarginTop(4f).setMarginBottom(10f))
    }
}
