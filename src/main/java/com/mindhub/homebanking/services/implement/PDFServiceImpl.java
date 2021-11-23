package com.mindhub.homebanking.services.implement;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.PDFService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
@AllArgsConstructor
public class PDFServiceImpl implements PDFService {

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public void generateAccountResume(HttpServletResponse response, Client client, String accountNumber) throws IOException {

        Account account = accountRepo.findByNumber(accountNumber).orElse(null);
        //La linea 56 perimte que una vez creado el pdf se guarde en la response,
        //eso hace que se descargue al encontrar la respuesta
        PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDocument = new PdfDocument((pdfWriter));

        Document document = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        String date = LocalDateTime.now().format(ISO_LOCAL_DATE);

        //Concepto similar a grid de css
        float col = 280f;
        float columnWidth[] = {col, col};

        Table table = new Table(columnWidth);

        table.setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255));
        table.addCell(new Cell().add(new Paragraph("MH|Brothers"))
                .setCharacterSpacing(0.2F)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddings(30f, 0f, 30f, 0f)
                .setFontSize(30f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Mind Hub Brothers\n#2025 HomeBanking\n" + date))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddings(30f, 10f, 30f, 0f)
                .setBorder(Border.NO_BORDER));

        float colWidth[] = {80f, 300f, 100f, 80f};
        Table clientTableInfo = new Table(columnWidth);

        clientTableInfo.addCell(new Cell(0, 4)
                .add(new Paragraph("Client Information"))
                .setBold()
                .setBorder(Border.NO_BORDER));

        clientTableInfo.addCell(new Cell()
                .add(new Paragraph("Client Name: "))
                .setBorder(Border.NO_BORDER));
        clientTableInfo.addCell(new Cell()
                .add(new Paragraph(client.getFullName()))
                .setBorder(Border.NO_BORDER));
        clientTableInfo.addCell(new Cell()
                .add(new Paragraph("Account N°: "))
                .setBorder(Border.NO_BORDER));
        clientTableInfo.addCell(new Cell()
                .add(new Paragraph(account.getNumber()))
                .setBorder(Border.NO_BORDER));
        clientTableInfo.addCell(new Cell()
                .add(new Paragraph("Date: "))
                .setBorder(Border.NO_BORDER));
        clientTableInfo.addCell(new Cell()
                .add(new Paragraph(date))
                .setBorder(Border.NO_BORDER));


        float itemInfoColWidth[] = {90f, 100f, 190f, 70f, 70f, 70f};
        Table dataTable = new Table(itemInfoColWidth);
        dataTable.setFontSize(10f);

        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Destination Account")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Concept")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Type")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Amount")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        dataTable.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(5, 71, 105))
                .setFontColor(new DeviceRgb(255, 255, 255))
                .add(new Paragraph("Balance")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(clientTableInfo);
        document.add(new Paragraph("\n"));
        dataTable.startNewRow();
        LinkedHashSet<Transaction> transactions = new LinkedHashSet<>(account.getTransactions());


        for (Transaction tr : transactions) {
            dataTable.setFontSize(10f);
            dataTable.addCell(new Cell().add(new Paragraph(tr.getDate().format(ISO_LOCAL_DATE))));
            dataTable.addCell(new Cell().add(new Paragraph(tr.getDestinationAccount())));
            dataTable.addCell(new Cell().add(new Paragraph(tr.getDescription())));
            Paragraph text = new Paragraph(tr.getType().name());
            dataTable.addCell(new Cell().add(text).setFontColor(
                    tr.getType().toString().equals("CREDIT") ? new DeviceRgb(0, 0, 255) : new DeviceRgb(204, 0, 0))
            );
            dataTable.addCell(new Cell().add(new Paragraph("$" + tr.getAmount())));
            dataTable.addCell(new Cell().add(new Paragraph("$" + tr.getCurrentBalance())));
            dataTable.startNewRow();
        }

        document.add(dataTable);
        Table footer = new Table(1).useAllAvailableWidth();

        Cell cell = new Cell().add(new Paragraph("This is a test doc"));
        cell.setBackgroundColor(ColorConstants.ORANGE);
        footer.addCell(cell);

        cell = new Cell().add(new Paragraph("MH | Brothers "));
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        footer.addCell(cell);


        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new TableFooterEventHandler(footer));
        document.close();
    }

    private static class TableFooterEventHandler implements IEventHandler {
        private Table table;

        public TableFooterEventHandler(Table table) {
            this.table = table;
        }

        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            new Canvas(canvas, new Rectangle(36, 20, page.getPageSize().getWidth() - 72, 50))
                    .add(table)
                    .close();
        }
    }

}
