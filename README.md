# FinTrack - Expense Tracker App

## Overview
FinTrack is a comprehensive Android expense tracking application built with Java and Android Studio. The app allows users to record, manage, and share their expenses with a modern, responsive interface.

## Features

### Core Functionality
- **Expense Recording**: Add new expenses with detailed information
- **Date & Time Selection**: Built-in DatePicker and TimePicker for precise timestamp recording
- **Photo Attachments**: Attach photos to expense records for receipts and documentation
- **Category Management**: Organize expenses by categories (Food & Dining, Transportation, Shopping, etc.)
- **Persistent Storage**: All data is stored locally using Room Database, ensuring data persistence across app restarts

### User Interface
- **Responsive Design**: Modern Material Design interface that adapts to different screen sizes
- **List View**: Clean, card-based list showing all recorded expenses
- **Floating Action Button**: Easy access to add new expenses
- **Share Functionality**: Share expense details via WhatsApp, Email, or other apps using implicit intents

### Data Management
- **Local Database**: Room Database for reliable data storage
- **CRUD Operations**: Create, Read, Update, and Delete expense records
- **Data Validation**: Input validation for required fields
- **Image Storage**: Photos are saved to internal storage for privacy

## Technical Architecture

### Design Pattern
- **MVC (Model-View-Controller)**: Clean separation of concerns
  - **Model**: Expense entity and Room Database
  - **View**: XML layouts and RecyclerView adapter
  - **Controller**: Activities handling user interactions

### Key Components
1. **MainActivity**: Main screen displaying expense list
2. **AddExpenseActivity**: Form for adding new expenses
3. **Expense**: Entity class representing expense data
4. **ExpenseDao**: Data Access Object for database operations
5. **ExpenseDatabase**: Room database configuration
6. **ExpenseAdapter**: RecyclerView adapter for displaying expenses

### Dependencies
- **Room Database**: For local data persistence
- **Material Design Components**: For modern UI elements
- **Glide**: For image loading and caching
- **AndroidX Libraries**: For modern Android development

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24 or higher
- Java 11

### Build Instructions
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

### Permissions Required
- `READ_EXTERNAL_STORAGE`: For accessing photos from gallery
- `WRITE_EXTERNAL_STORAGE`: For saving photos to device

## Usage Guide

### Adding an Expense
1. Tap the floating action button (+)
2. Fill in the expense details:
   - Title (required)
   - Amount (required)
   - Select date and time
   - Choose category
   - Add optional notes
   - Attach photo (optional)
3. Tap "Save Expense"

### Managing Expenses
- **View**: All expenses are displayed in a scrollable list
- **Share**: Tap the share icon to share expense details
- **Delete**: Tap the delete icon to remove an expense

### Sharing Expenses
The app supports sharing expense details through:
- WhatsApp
- Email
- SMS
- Any other app that accepts text sharing

## Project Structure

```
app/src/main/java/com/example/fintrack/
├── MainActivity.java              # Main activity with expense list
├── AddExpenseActivity.java        # Activity for adding new expenses
├── Expense.java                   # Entity class for expense data
├── ExpenseDao.java               # Data Access Object interface
├── ExpenseDatabase.java          # Room database configuration
└── ExpenseAdapter.java           # RecyclerView adapter

app/src/main/res/
├── layout/
│   ├── activity_main.xml         # Main activity layout
│   ├── activity_add_expense.xml  # Add expense form layout
│   └── expense_item.xml          # Individual expense item layout
├── drawable/                     # Icons and drawable resources
├── values/
│   ├── colors.xml               # Color definitions
│   ├── strings.xml              # String resources
│   └── themes.xml               # App theme configuration
└── mipmap-*/                    # App icons for different densities
```

## Features Implementation

### 1. List of Recorded Items/Entries ✅
- RecyclerView displays all expenses in a scrollable list
- Each expense shows title, amount, date, time, and category
- Photos are displayed when available

### 2. Creation Activity ✅
- AddExpenseActivity provides a comprehensive form
- Input validation for required fields
- User-friendly interface with Material Design components

### 3. DatePicker and TimePicker ✅
- Native Android DatePickerDialog for date selection
- Native Android TimePickerDialog for time selection
- Default values set to current date and time

### 4. Photo Attachment ✅
- Gallery picker integration
- Image compression and storage
- Display in expense list and detail views

### 5. Local Data Storage ✅
- Room Database for persistent storage
- Data survives app restarts
- Efficient CRUD operations

### 6. Sharing Functionality ✅
- Implicit intents for sharing
- Support for WhatsApp, Email, SMS, and other apps
- Formatted expense details in share text

## Design Principles

### Responsive Design
- Uses ConstraintLayout and LinearLayout for flexible layouts
- Material Design components for consistent UI
- Adaptive to different screen sizes and orientations

### Sound Architecture
- MVC pattern implementation
- Separation of concerns between UI, business logic, and data
- Modular code structure for maintainability

### User Experience
- Intuitive navigation with floating action button
- Clear visual hierarchy with cards and typography
- Immediate feedback for user actions
- Error handling and validation

## Future Enhancements

Potential improvements for the app:
- Expense categories with icons
- Budget tracking and alerts
- Export functionality (PDF, CSV)
- Cloud backup integration
- Expense analytics and charts
- Multi-currency support
- Receipt OCR for automatic data extraction

## Testing

The app includes:
- Unit tests for database operations
- Instrumented tests for UI components
- Manual testing for all user flows

## License

This project is developed for educational purposes as part of a mobile application development course.

---

**Developer**: [Your Name]
**Course**: Mobile Application Development
**Institution**: [Your Institution]
**Date**: [Current Date] 