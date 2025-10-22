package com.mpmt.backend.mail;

import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender sender;

    @Value("${app.frontend.base-url:http://localhost:8080}")
    private String frontendBaseUrl; // déjà OK pour dev

    @Value("${app.mail.from:}")
    private String fromEmail; // expéditeur configurable (Single Sender SendGrid)

    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }

    /** Envoie un e-mail de notification d'assignation (best-effort, ne jette pas). */
    public void sendTaskAssignedEmail(Task task, User assignee, @Nullable User assigner) {
        if (assignee == null || assignee.getEmail() == null) return;

        String subject = "[MPMT] Nouvelle assignation — Tâche #" + task.getId();
        String assignerName = (assigner != null && assigner.getUsername() != null)
                ? assigner.getUsername()
                : "un membre du projet";

        String link = buildProjectLink(task);

        String body = """
                Bonjour %s,

                Vous avez été assigné·e à la tâche :
                  • ID : %d
                  • Titre : %s

                Assignée par : %s
                Lien : %s

                -- MPMT
                """.formatted(
                safe(assignee.getUsername()),
                task.getId(),
                safe(task.getTitle()),
                assignerName,
                link
        );

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                msg.setFrom(fromEmail); // requis par SendGrid Single Sender
            }
            msg.setTo(assignee.getEmail());
            msg.setSubject(subject);
            msg.setText(body);
            sender.send(msg);
        } catch (Exception e) {
            System.out.println("[MailService] Échec d’envoi e-mail d’assignation: " + e.getMessage());
        }
    }

    private String buildProjectLink(Task task) {
        try {
            if (task != null && task.getProject() != null && task.getProject().getId() != null) {
                return frontendBaseUrl + "/projects/" + task.getProject().getId();
            }
        } catch (Exception ignored) {}
        return frontendBaseUrl + "/dashboard";
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? "-" : s; }
}
