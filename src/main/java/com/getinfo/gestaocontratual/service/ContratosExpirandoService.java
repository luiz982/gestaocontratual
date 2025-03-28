package com.getinfo.gestaocontratual.service;

import com.getinfo.gestaocontratual.entities.Contrato;
import com.getinfo.gestaocontratual.repository.ContratoRepository;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContratosExpirandoService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ContratoRepository contratoRepository;

    @Scheduled(cron = "0 09 12 * * ?")
    public void enviarEmailsContratosExpirando() {
        //LocalDate hoje = LocalDate.now();
        LocalDate hoje = LocalDate.of(2025, 3, 1);
        LocalDate prazo = hoje.plusDays(60);

        List<Contrato> contratosExpirando = contratoRepository.findByDtFimBetween(hoje, prazo);

        for (Contrato contrato : contratosExpirando) {
            //enviarEmail(contrato.getEmailResponsavel(), contrato);
            enviarEmail("luiz.jacson@souunit.com.br", contrato);
        }
    }

    public void enviarEmail(String destinatario, Contrato contrato) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("Aviso: Seu contrato está prestes a expirar!");
        mensagem.setText("O contrato de número " + contrato.getNumContrato() +
                " expira em " + contrato.getDtFim() + ". Por favor, tome as devidas providências.");
        mailSender.send(mensagem);
    }
}