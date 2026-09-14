# 💰 SplitWise — Smart Group Expense Manager

A modern **FinTech-style group expense management app** built with **Flutter and Firebase**. Split expenses, track balances, scan receipts, and simplify settlements with a clean, premium, and user-friendly interface.

> **Split smarter. Settle easier.**

---

## ✨ Overview

Managing shared expenses with friends, roommates, trips, or teams can quickly become complicated.

**SplitWise** makes it simple by allowing users to create groups, add expenses, split bills, track balances, and see exactly who owes whom.

The app combines a modern **Material 3 UI**, Firebase backend, Provider state management, and OCR-powered receipt scanning to provide a smooth and reliable expense-sharing experience.

---

## 🚀 Features

* 👥 **Group Management** — Create groups and add members.
* 💸 **Expense Tracking** — Add and manage shared expenses.
* ⚖️ **Smart Splitting** — Split expenses equally, by exact amounts, or percentages.
* 📊 **Balance Tracking** — Clearly see who owes money and who should receive money.
* 🔄 **Debt Simplification** — Reduce multiple transactions into simpler settlements.
* 🧾 **Receipt Scanning** — Extract expense information from receipts using OCR.
* 📸 **Receipt Storage** — Upload and securely store receipt images.
* 🔐 **Authentication** — Firebase-based user authentication.
* ☁️ **Cloud Database** — Real-time expense and group data using Firestore.
* 🌙 **Dark Mode** — Premium light and OLED-friendly dark themes.
* ✨ **Smooth Animations** — Micro-interactions and staggered animations.
* ⏳ **Shimmer Loading** — Modern loading states instead of boring spinners.
* 📱 **Responsive UI** — Designed for different mobile screen sizes.
* 🛡️ **Error Handling** — User-friendly error messages and SnackBars.

---

## 🛠️ Tech Stack

### Frontend

* Flutter
* Dart
* Material 3

### Backend

* Firebase Authentication
* Cloud Firestore
* Firebase Storage
* Firebase Cloud Functions

### State Management

* Provider

### Packages

* `google_fonts`
* `flutter_animate`
* `cached_network_image`
* `shimmer`
* `google_mlkit_text_recognition`
* `image_picker`

---

## 🏗️ Project Architecture

The project follows a clean and scalable structure:

```text
lib/
│
├── models/
│   └── Data models
│
├── providers/
│   └── State management & business logic
│
├── services/
│   └── Firebase, OCR & backend services
│
├── screens/
│   ├── auth/
│   ├── home/
│   ├── groups/
│   ├── expenses/
│   ├── balances/
│   ├── activity/
│   └── profile/
│
├── widgets/
│   ├── common/
│   ├── cards/
│   ├── forms/
│   └── animations/
│
├── theme/
│   └── App theme & design system
│
└── main.dart
```

The UI is kept separate from business logic, with **Provider and Services** responsible for application state and operations.

---

## 🗄️ Firestore Data Structure

```text
users
 ├── name
 ├── email
 └── photoUrl

groups
 ├── name
 ├── memberIds
 ├── createdBy
 └── createdAt

groups/{groupId}/expenses
 ├── description
 ├── totalAmount
 ├── paidBy
 ├── splitType
 ├── splits
 ├── receiptImageUrl
 └── createdAt

groups/{groupId}/balances
 ├── userId
 └── netBalance
```

---

## 🎨 UI/UX

The application follows a premium FinTech design language:

* Material 3
* Poppins / Inter typography
* Soft rounded cards
* Minimal borders
* Subtle shadows
* Clean whitespace
* Premium light and dark themes
* Smooth Hero animations
* Staggered list animations
* Shimmer loading states
* Friendly empty states
* Accessible tap targets

The primary goal is to make financial information **easy to understand at a glance**.

---

## 🧾 Receipt OCR

Users can capture or upload a receipt using the device camera/gallery.

The application uses **Google ML Kit Text Recognition** to extract information such as:

* Merchant name
* Total amount
* Date
* Other relevant text

Users can review and edit the extracted information before creating the expense.

---

## ⚖️ Debt Simplification

The application calculates group balances and simplifies settlements to reduce unnecessary transactions.

For example:

```text
Before:

A → B   ₹300
B → C   ₹200
C → A   ₹100

After Simplification:

A → B   ₹200
C → A   ₹100
```

Complex balance calculations are designed as pure functions and can be moved to **Firebase Cloud Functions** to maintain a single source of truth.

---

## 🔥 Firebase

Firebase is used for:

* User authentication
* Group management
* Expense storage
* Real-time balance updates
* Receipt image storage
* Cloud-based financial calculations

All Firebase operations should be wrapped with proper error handling to prevent silent failures.

---

## 📦 Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/splitwise.git
```

### 2. Navigate to the project

```bash
cd splitwise
```

### 3. Install dependencies

```bash
flutter pub get
```

### 4. Configure Firebase

Create a Firebase project and connect it to your Flutter application.

Configure:

* Firebase Authentication
* Cloud Firestore
* Firebase Storage
* Firebase Cloud Functions

### 5. Run the application

```bash
flutter run
```

---

## 🔑 Configuration

Do not commit sensitive Firebase credentials, API keys, or private configuration files to the repository.

Use the appropriate Firebase configuration for:

* Android
* iOS
* Web

---

## 🗺️ Roadmap

* [x] Project architecture
* [ ] Authentication
* [ ] User profile
* [ ] Group creation
* [ ] Member management
* [ ] Add expense
* [ ] Expense splitting
* [ ] Receipt OCR
* [ ] Balance calculation
* [ ] Debt simplification
* [ ] Settlement tracking
* [ ] Cloud Functions
* [ ] Push notifications
* [ ] Advanced analytics
* [ ] Production release

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Test your changes.
5. Create a Pull Request.

---

## 📄 License

This project is currently developed for **learning, portfolio, and educational purposes**.

---

## 👨‍💻 Developer

Developed with ❤️ using **Flutter + Firebase**.

**Split smarter. Settle easier.**

