package com.example.houserentalapp.utils;

public class Constants {

    // Firebase Database Paths
    public static final String DB_USERS = "users";
    public static final String DB_PROPERTIES = "properties";
    public static final String DB_PROPERTY_IMAGES = "propertyImages";
    public static final String DB_FAVOURITES = "favourites";
    public static final String DB_CONVERSATIONS = "conversations";
    public static final String DB_MESSAGES = "messages";
    public static final String DB_APPLICATIONS = "applications";
    public static final String DB_VISITS = "visits";
    public static final String DB_LEASES = "leases";
    public static final String DB_PAYMENTS = "payments";
    public static final String DB_REVIEWS = "reviews";
    public static final String DB_MAINTENANCE = "maintenanceTickets";
    public static final String DB_NOTIFICATIONS = "notifications";
    public static final String DB_REPORTS = "reports";
    public static final String DB_ANALYTICS_PLATFORM = "analytics/platform";
    public static final String DB_ANALYTICS_PROPERTIES = "analytics/properties";

    // Firebase Storage Paths
    public static final String STORAGE_PROPERTY_IMAGES = "property_images/";
    public static final String STORAGE_PROFILE_IMAGES = "profile_images/";
    public static final String STORAGE_DOCUMENTS = "documents/";
    public static final String STORAGE_MAINTENANCE = "maintenance_images/";

    // User Roles
    public static final String ROLE_TENANT = "tenant";
    public static final String ROLE_LANDLORD = "landlord";
    public static final String ROLE_ADMIN = "admin";

    // Property Types
    public static final String TYPE_HOME = "Home";
    public static final String TYPE_FLAT = "Flat";
    public static final String TYPE_ROOM = "Room";
    public static final String TYPE_STUDIO = "Studio";
    public static final String TYPE_VILLA = "Villa";
    public static final String TYPE_OFFICE = "Office";

    // Application Statuses
    public static final String APP_STATUS_PENDING = "pending";
    public static final String APP_STATUS_REVIEWING = "reviewing";
    public static final String APP_STATUS_ACCEPTED = "accepted";
    public static final String APP_STATUS_REJECTED = "rejected";
    public static final String APP_STATUS_WITHDRAWN = "withdrawn";

    // Visit Statuses
    public static final String VISIT_STATUS_PENDING = "pending";
    public static final String VISIT_STATUS_CONFIRMED = "confirmed";
    public static final String VISIT_STATUS_REJECTED = "rejected";
    public static final String VISIT_STATUS_COMPLETED = "completed";
    public static final String VISIT_STATUS_CANCELLED = "cancelled";

    // Lease Statuses
    public static final String LEASE_STATUS_ACTIVE = "active";
    public static final String LEASE_STATUS_EXPIRED = "expired";
    public static final String LEASE_STATUS_TERMINATED = "terminated";

    // Payment Statuses
    public static final String PAYMENT_STATUS_DUE = "due";
    public static final String PAYMENT_STATUS_PAID = "paid";
    public static final String PAYMENT_STATUS_OVERDUE = "overdue";
    public static final String PAYMENT_STATUS_WAIVED = "waived";

    // Maintenance Priorities
    public static final String PRIORITY_LOW = "low";
    public static final String PRIORITY_MEDIUM = "medium";
    public static final String PRIORITY_HIGH = "high";
    public static final String PRIORITY_URGENT = "urgent";

    // Maintenance Statuses
    public static final String MAINT_STATUS_OPEN = "open";
    public static final String MAINT_STATUS_IN_PROGRESS = "in_progress";
    public static final String MAINT_STATUS_RESOLVED = "resolved";
    public static final String MAINT_STATUS_CLOSED = "closed";

    // Furnishing Types
    public static final String FURNISHED_NONE = "Unfurnished";
    public static final String FURNISHED_SEMI = "Semi-Furnished";
    public static final String FURNISHED_FULL = "Fully Furnished";

    // Currencies
    public static final String CURRENCY_INR = "INR";
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_GBP = "GBP";
    public static final String CURRENCY_AED = "AED";
    public static final String CURRENCY_SGD = "SGD";
    public static final String CURRENCY_MYR = "MYR";

    // Currency Symbols
    public static String getCurrencySymbol(String currency) {
        if (currency == null) return "₹";
        switch (currency) {
            case CURRENCY_USD: return "$";
            case CURRENCY_EUR: return "€";
            case CURRENCY_GBP: return "£";
            case CURRENCY_AED: return "د.إ";
            case CURRENCY_SGD: return "S$";
            case CURRENCY_MYR: return "RM";
            case CURRENCY_INR:
            default: return "₹";
        }
    }

    // Intent Extras
    public static final String EXTRA_PROPERTY_ID = "extra_property_id";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_CONVERSATION_ID = "extra_conversation_id";
    public static final String EXTRA_APPLICATION_ID = "extra_application_id";
    public static final String EXTRA_LEASE_ID = "extra_lease_id";
    public static final String EXTRA_VISIT_ID = "extra_visit_id";
    public static final String EXTRA_TICKET_ID = "extra_ticket_id";
    public static final String EXTRA_PROPERTY_TYPE = "extra_property_type";
    public static final String EXTRA_FILTER_KEY = "extra_filter_key";

    // Notification Types
    public static final String NOTIF_NEW_APPLICATION = "new_application";
    public static final String NOTIF_APPLICATION_UPDATE = "application_update";
    public static final String NOTIF_NEW_MESSAGE = "new_message";
    public static final String NOTIF_VISIT_REQUEST = "visit_request";
    public static final String NOTIF_VISIT_UPDATE = "visit_update";
    public static final String NOTIF_PAYMENT_DUE = "payment_due";
    public static final String NOTIF_MAINTENANCE_UPDATE = "maintenance_update";
    public static final String NOTIF_LEASE_CREATED = "lease_created";

    // SharedPreferences Keys (managed by SessionManager)
    public static final String PREF_FILE = "rental_prefs";
    public static final String PREF_UID = "uid";
    public static final String PREF_ROLE = "role";
    public static final String PREF_NAME = "name";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_PROFILE_IMAGE = "profile_image";
    public static final String PREF_CURRENCY = "preferred_currency";
    public static final String PREF_DARK_MODE = "dark_mode";

    // Pagination
    public static final int PAGE_SIZE = 20;

    // Image quality minimum
    public static final int MIN_IMAGE_WIDTH = 400;
    public static final int MIN_IMAGE_HEIGHT = 300;

    // Report Reasons
    public static final String REPORT_FAKE_LISTING = "Fake Listing";
    public static final String REPORT_WRONG_PRICE = "Wrong Price";
    public static final String REPORT_ALREADY_RENTED = "Already Rented";
    public static final String REPORT_INAPPROPRIATE = "Inappropriate Content";
    public static final String REPORT_SPAM = "Spam";
    public static final String REPORT_OTHER = "Other";
}
