package com.uasz.daos.emploitemps.service;

import com.uasz.daos.emploitemps.model.Notification;

public interface EmailService {
    void sendEmail(Notification notification);
}
