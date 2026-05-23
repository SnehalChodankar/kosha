# Kosha Future Implementation Roadmap

This document outlines the brainstormed features for future development of the Kosha privacy-first password manager.

## 1. Seamless User Experience
*   **Android Autofill Integration**: Hook into Android's native `AutofillService`. Allows Kosha to natively prompt the user to fill passwords directly inside Chrome or other Android apps, bypassing the need to copy/paste.
*   **Custom Fields / Notes**: Expand the vault entry schema to store Website URLs (which ties into Autofill), Security Questions, or a general "Secure Notes" text block.
*   **Brand Auto-Linking**: When a user selects a known brand (e.g., Netflix), its official website URL is automatically linked to the entry to seamlessly support Autofill without manual entry.

## 2. Advanced Security & Privacy
*   **Duress PIN / Self-Destruct**: A "fake" PIN feature. If forced to unlock the app, entering the Duress PIN opens a completely empty decoy vault or silently wipes the database.
*   **Inactivity Auto-Lock Timer**: An automatic lock that triggers if the app is left open on the screen without any interactions for a configurable amount of time (e.g., 1 minute, 5 minutes).

## 3. All-in-One Capabilities
*   **Built-in 2FA Authenticator (TOTP)**: Store 2FA secret keys in Kosha and generate the rotating 6-digit codes right next to passwords, eliminating the need for external apps like Google Authenticator.
*   **Identity & Cards Storage**: Specific, visually distinct templates for storing Credit Cards (16-digits, CVV, expiry) or Identity Documents (Social Security numbers, Passport info).

## 4. Password Health
*   **Offline Password Breach Checking (k-Anonymity)**: Alert users if their password is in a known data leak by securely querying external databases using only the first 5 characters of a hashed password, completing the check locally to preserve zero-tracking privacy.
