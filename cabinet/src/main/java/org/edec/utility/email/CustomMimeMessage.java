package org.edec.utility.email;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CustomMimeMessage extends MimeMessage {

    //for unique email id generation
    private static final long twepoch = 1288834974657L;
    private static final long sequenceBits = 17;
    private static final long sequenceMax = 65536;
    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;

    Session session;

    public CustomMimeMessage(Session session) {
        super(session);
        this.session=session;
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        setHeader("Message-ID", "<" + getUniqueMessageIDValue(session) + ">");
    }

    public static String getUniqueMessageIDValue(Session ssn) {
        String suffix = null;

        InternetAddress addr = InternetAddress.getLocalAddress(ssn);
        if (addr != null)
            suffix = addr.getAddress();
        else {
            suffix = "dec-noreply@sfu-kras.ru"; // worst-case default
        }

        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<id>.<currentTime>.JavaMail.<suffix>
        s.append(s.hashCode()).append('.').append(generateLongId()).append('.').
                append(System.currentTimeMillis()).append('.').
                 append("JavaMail.").
                 append(suffix);
        return s.toString();
    }

    private static synchronized Long generateLongId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << sequenceBits) | sequence;
        return id;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
