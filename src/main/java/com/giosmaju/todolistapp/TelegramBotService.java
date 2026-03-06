package com.giosmaju.todolistapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotService extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    @Value("${telegrambot.username}")
    private String botUsername;

    @Value("${telegrambot.token}")
    private String botToken;

    @Autowired
    private TodoRepository todoRepository;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            logger.info("TelegramBotService registered successfully with username: {}", botUsername);
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            logger.info("Received message: {} from chat: {}", messageText, chatId);

            String response = processCommand(messageText);
            sendMessage(chatId, response);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private String processCommand(String messageText) {
        String[] parts = messageText.split(" ", 2);
        String command = parts[0].toLowerCase();

        return switch (command) {
            case "/start" -> handleStart();
            case "/list" -> handleList();
            case "/add" -> parts.length > 1 ? handleAdd(parts[1]) : "Uso: /add <título> - <descripción>\nEjemplo: /add Comprar leche - Ir al supermercado";
            case "/done" -> parts.length > 1 ? handleDone(parts[1]) : "Uso: /done <id>";
            case "/delete" -> parts.length > 1 ? handleDelete(parts[1]) : "Uso: /delete <id>";
            case "/help" -> handleHelp();
            default -> "Comando no reconocido. Usa /help para ver los comandos disponibles.";
        };
    }

    private String handleStart() {
        return """
                ¡Hola! Soy TodoListDockerBot

                Puedo ayudarte a gestionar tus tareas. Usa estos comandos:
                /list - Ver todas tus tareas
                /add <título> - <descripción> - Crear nueva tarea
                /done <id> - Marcar tarea como completada
                /delete <id> - Eliminar tarea
                /help - Ver esta ayuda

                Ejemplo: /add Comprar leche - Ir al supermercado
                """;
    }
    private String handleList() {        List<Todo> todos = todoRepository.findAll();

        if (todos.isEmpty()) {
            return "No tienes tareas. ¡Usa /add para crear una!";
        }

        StringBuilder response = new StringBuilder("Tus tareas:\n\n");
        for (Todo todo : todos) {
            String status = todo.isCompleted() ? "[COMPLETADA]" : "[PENDIENTE]";
            response.append(String.format("%s ID %d: %s\n   %s\n",
                    status, todo.getId(), todo.getTitle(), todo.getDescription()));
        }
        return response.toString();
    }

    private String handleAdd(String description) {
        String[] parts = description.split(" - ", 2);
        String title = parts[0].trim();
        String descr = parts.length > 1 ? parts[1].trim() : "Sin descripción";

        Todo newTodo = new Todo(title, descr);
        Todo saved = todoRepository.save(newTodo);

        return String.format("Tarea creada!\nID: %d\nTítulo: %s\nDescripción: %s",
                saved.getId(), saved.getTitle(), saved.getDescription());
    }

    private String handleDone(String idStr) {
        try {
            long id = Long.parseLong(idStr.trim());
            if (todoRepository.existsById(id)) {
                Todo todo = todoRepository.findById(id).get();
                todo.setCompleted(true);
                todoRepository.save(todo);
                return String.format("Tarea completada: %s", todo.getTitle());
            } else {
                return "No se encontró una tarea con ese ID.";
            }
        } catch (NumberFormatException e) {
            return "El ID debe ser un número válido.";
        }
    }

    private String handleDelete(String idStr) {
        try {
            long id = Long.parseLong(idStr.trim());
            if (todoRepository.existsById(id)) {
                Todo todo = todoRepository.findById(id).get();
                todoRepository.deleteById(id);
                return String.format("Tarea eliminada: %s", todo.getTitle());
            } else {
                return "No se encontró una tarea con ese ID.";
            }
        } catch (NumberFormatException e) {
            return "El ID debe ser un número válido.";
        }
    }

    private String handleHelp() {
        return """
                Ayuda - Comandos disponibles:

                /start - Ver bienvenida
                /list - Ver todas tus tareas
                /add <título> - <descripción> - Crear nueva tarea
                /done <id> - Marcar tarea como completada
                /delete <id> - Eliminar tarea
                /help - Ver esta ayuda

                Ejemplo: /add Comprar leche - Ir al supermercado
                """;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            logger.info("Sent message to chat {}: {}", chatId, text.substring(0, Math.min(50, text.length())));
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
        }
    }
}