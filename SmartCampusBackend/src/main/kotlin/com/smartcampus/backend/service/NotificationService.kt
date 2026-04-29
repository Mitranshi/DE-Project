package com.smartcampus.backend.service

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationService {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getNotifications(userId: Int): List<NotificationResponse> {
        return transaction {
            Notifications.select { Notifications.userId eq userId }
                .orderBy(Notifications.createdAt to SortOrder.DESC)
                .map { row ->
                    NotificationResponse(
                        id = row[Notifications.id],
                        title = row[Notifications.title],
                        message = row[Notifications.message],
                        type = row[Notifications.type],
                        isRead = row[Notifications.isRead],
                        createdAt = row[Notifications.createdAt].format(formatter)
                    )
                }
        }
    }

    fun markAsRead(notificationId: Int): MessageResponse {
        transaction {
            Notifications.update({ Notifications.id eq notificationId }) {
                it[isRead] = true
            }
        }
        return MessageResponse("Notification marked as read")
    }

    fun markAllAsRead(userId: Int): MessageResponse {
        transaction {
            Notifications.update({ Notifications.userId eq userId }) {
                it[isRead] = true
            }
        }
        return MessageResponse("All notifications marked as read")
    }

    fun createNotification(userId: Int, title: String, message: String, type: String) {
        transaction {
            Notifications.insert {
                it[Notifications.userId] = userId
                it[Notifications.title] = title
                it[Notifications.message] = message
                it[Notifications.type] = type
                it[isRead] = false
                it[createdAt] = LocalDateTime.now()
            }
        }
    }

    fun getUnreadCount(userId: Int): Int {
        return transaction {
            Notifications.select { (Notifications.userId eq userId) and (Notifications.isRead eq false) }
                .count().toInt()
        }
    }
}
