package com.getinfo.gestaocontratual.service;

import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.time.ZoneId;

@Service
public class ContratosExpirandoService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContratoRepository contratoRepository;

    private static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    @Scheduled(cron = "0 27 11 * * ?")
    public void enviarEmailsContratosExpirando() throws MessagingException {
        LocalDate hoje = LocalDate.now();
        LocalDate prazo = hoje.plusDays(60);

        List<Contrato> contratos = contratoRepository.findByDtFimBetween(hoje, prazo);
        if (contratos.isEmpty()) {
            return;
        }

        StringBuilder html = new StringBuilder();
        String logoUrl = "https://www.getinfo.net.br/static/media/logo-getinfo2.46e29e79.png";
        html.append("<html><body>");
        html.append("<div style='text-align:center;margin-bottom:20px;'>")
                .append("<img src=\"" + logoUrl + "\" alt=\"Getinf\" width=\"210\" height=\"75\">")
                .append("</div>");
        html.append("<h3>Contratos prestes a expirar</h3>");
        html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse;width:100%'>")
                .append("<tr>")
                .append("<th>Nº Contrato</th>")
                .append("<th>Empresa</th>")
                .append("<th>Data Início</th>")
                .append("<th>Data Fim</th>")
                .append("</tr>");
        for (Contrato c : contratos) {
            Date inicio = c.getDtInicio();
            Date fim = c.getDtFim();

            html.append("<tr>")
                    .append("<td>").append(c.getNumContrato()).append("</td>")
                    .append("<td>").append(c.getIdContratante().getRazaoSocial()).append("</td>")
                    .append("<td>").append(df.format(inicio)).append("</td>")
                    .append("<td>").append(df.format(fim)).append("</td>")
                    .append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo("luiz.jacson@souunit.com.br");
        helper.setSubject("Aviso: Contratos próximos do vencimento");
        helper.setText(html.toString(), true);

        mailSender.send(message);
    }
}