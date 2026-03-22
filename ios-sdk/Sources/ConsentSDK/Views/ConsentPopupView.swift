import SwiftUI

/// Main consent popup SwiftUI view.
///
/// Displays a full-screen dimmed overlay with a centered card containing:
/// - Title with icon
/// - Age input field
/// - Scrollable purpose checklist
/// - Update Consent / Decline buttons
struct ConsentPopupView: View {
    let state: ConsentState
    let theme: ConsentTheme
    let onAgeChanged: (String) -> Void
    let onPurposeToggled: (String) -> Void
    let onSubmit: () -> Void
    let onDecline: () -> Void
    let onRetry: () -> Void
    let onDismiss: () -> Void

    @FocusState private var isAgeFocused: Bool

    var body: some View {
        ZStack {
            // Dimmed overlay
            theme.overlayColor
                .ignoresSafeArea()
                .onTapGesture { onDismiss() }

            // Popup card
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 20) {
                        headerSection
                        contentSection
                    }
                    .padding(24)
                }

                if !state.isLoading && state.error == nil || !state.purposes.isEmpty {
                    buttonsSection
                        .padding(.horizontal, 24)
                        .padding(.bottom, 24)
                }
            }
            .background(theme.backgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
            .shadow(color: .black.opacity(0.15), radius: 20, x: 0, y: 10)
            .padding(.horizontal, 16)
            .padding(.vertical, 40)
        }
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: 12) {
            // Decorative icon
            ZStack {
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(
                        LinearGradient(
                            colors: [theme.primaryColor, theme.primaryColor.opacity(0.7)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .frame(width: 48, height: 48)

                Image(systemName: "checkmark.shield.fill")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(theme.buttonTextColor)
            }

            Text("Consent Preferences")
                .font(.system(size: 22, weight: .bold))
                .foregroundColor(theme.textColor)
                .multilineTextAlignment(.center)

            Text("Please review and update your consent preferences below.")
                .font(.system(size: 13))
                .foregroundColor(theme.textColor.opacity(0.6))
                .multilineTextAlignment(.center)
                .padding(.horizontal, 16)
        }
    }

    // MARK: - Content

    @ViewBuilder
    private var contentSection: some View {
        if state.isLoading {
            loadingView
        } else if let error = state.error, state.purposes.isEmpty {
            errorView(error: error)
        } else if state.purposes.isEmpty {
            emptyView
        } else {
            purposeFormSection
        }
    }

    private var purposeFormSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Age input
            ageInputSection

            // Error banner
            if let error = state.error {
                errorBanner(error: error)
            }

            // Purposes
            VStack(alignment: .leading, spacing: 8) {
                Text("Select Purposes")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(theme.textColor)

                ForEach(state.purposes) { purpose in
                    PurposeRow(
                        purpose: purpose,
                        isSelected: state.selectedPurposes.contains(purpose.id),
                        theme: theme,
                        onToggle: { onPurposeToggled(purpose.id) }
                    )
                }
            }

            // Purpose validation error
            if let purposeError = state.validationErrors["purposes"] {
                Text(purposeError)
                    .font(.system(size: 12))
                    .foregroundColor(theme.errorColor)
            }
        }
    }

    private var ageInputSection: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Your Age")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(theme.textColor)

            TextField("Enter your age", text: Binding(
                get: { state.age },
                set: { newValue in
                    let filtered = newValue.filter { $0.isNumber }
                    onAgeChanged(filtered)
                }
            ))
            .keyboardType(.numberPad)
            .focused($isAgeFocused)
            .padding(12)
            .background(theme.inputBackgroundColor)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(
                        state.validationErrors["age"] != nil
                            ? theme.errorColor
                            : (isAgeFocused ? theme.primaryColor : theme.inputBorderColor),
                        lineWidth: 1
                    )
            )
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .foregroundColor(theme.textColor)

            if let ageError = state.validationErrors["age"] {
                Text(ageError)
                    .font(.system(size: 12))
                    .foregroundColor(theme.errorColor)
                    .padding(.leading, 4)
            }
        }
    }

    // MARK: - Buttons

    private var buttonsSection: some View {
        VStack(spacing: 10) {
            // Primary: Update Consent
            Button(action: {
                isAgeFocused = false
                onSubmit()
            }) {
                HStack {
                    if state.isSubmitting {
                        ProgressView()
                            .tint(theme.buttonTextColor)
                            .scaleEffect(0.8)
                        Text("Submitting...")
                            .font(.system(size: 15, weight: .semibold))
                    } else {
                        Text("Update Consent")
                            .font(.system(size: 15, weight: .semibold))
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(
                    state.isSubmitting
                        ? theme.primaryColor.opacity(0.5)
                        : theme.primaryColor
                )
                .foregroundColor(theme.buttonTextColor)
                .clipShape(RoundedRectangle(cornerRadius: theme.buttonRadius, style: .continuous))
            }
            .disabled(state.isSubmitting)

            // Secondary: Decline
            Button(action: onDecline) {
                Text("Decline")
                    .font(.system(size: 15, weight: .medium))
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .overlay(
                        RoundedRectangle(cornerRadius: theme.buttonRadius, style: .continuous)
                            .stroke(theme.secondaryButtonColor, lineWidth: 1)
                    )
                    .foregroundColor(theme.secondaryButtonColor)
            }
            .disabled(state.isSubmitting)
        }
    }

    // MARK: - Loading/Error/Empty

    private var loadingView: some View {
        VStack(spacing: 12) {
            Spacer()
            ProgressView()
                .scaleEffect(1.2)
                .tint(theme.primaryColor)
            Text("Loading consent options...")
                .font(.system(size: 14))
                .foregroundColor(theme.textColor.opacity(0.6))
            Spacer()
        }
        .frame(minHeight: 200)
    }

    private func errorView(error: String) -> some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 40))
                .foregroundColor(theme.errorColor)

            Text(error)
                .font(.system(size: 14))
                .foregroundColor(theme.errorColor)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 16)

            Button("Retry") { onRetry() }
                .padding(.horizontal, 24)
                .padding(.vertical, 10)
                .background(theme.primaryColor)
                .foregroundColor(theme.buttonTextColor)
                .clipShape(RoundedRectangle(cornerRadius: theme.buttonRadius))
            Spacer()
        }
        .frame(minHeight: 200)
    }

    private var emptyView: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "doc.text")
                .font(.system(size: 40))
                .foregroundColor(theme.textColor.opacity(0.5))

            Text("No consent options available")
                .font(.system(size: 16, weight: .medium))
                .foregroundColor(theme.textColor)

            Button("Close") { onDismiss() }
                .padding(.horizontal, 24)
                .padding(.vertical, 10)
                .background(theme.primaryColor)
                .foregroundColor(theme.buttonTextColor)
                .clipShape(RoundedRectangle(cornerRadius: theme.buttonRadius))
            Spacer()
        }
        .frame(minHeight: 200)
    }

    private func errorBanner(error: String) -> some View {
        HStack {
            Image(systemName: "exclamationmark.circle.fill")
                .foregroundColor(theme.errorColor)
                .font(.system(size: 14))
            Text(error)
                .font(.system(size: 12))
                .foregroundColor(theme.errorColor)
        }
        .padding(12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(theme.errorColor.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

// MARK: - Purpose Row

struct PurposeRow: View {
    let purpose: Purpose
    let isSelected: Bool
    let theme: ConsentTheme
    let onToggle: () -> Void

    var body: some View {
        Button(action: {
            if !purpose.mandatory { onToggle() }
        }) {
            HStack(alignment: .top, spacing: 12) {
                // Checkbox
                ZStack {
                    RoundedRectangle(cornerRadius: 5)
                        .stroke(
                            isSelected ? theme.checkboxColor : theme.inputBorderColor,
                            lineWidth: 1.5
                        )
                        .frame(width: 22, height: 22)

                    if isSelected {
                        RoundedRectangle(cornerRadius: 5)
                            .fill(theme.checkboxColor)
                            .frame(width: 22, height: 22)

                        Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(theme.buttonTextColor)
                    }
                }
                .padding(.top, 2)
                .opacity(purpose.mandatory ? 0.7 : 1.0)

                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(purpose.name)
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(theme.textColor)

                        if purpose.mandatory {
                            Text("Required")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(theme.mandatoryBadgeColor)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(theme.mandatoryBadgeColor.opacity(0.15))
                                .clipShape(RoundedRectangle(cornerRadius: 4))
                        }
                    }

                    Text(purpose.description)
                        .font(.system(size: 12))
                        .foregroundColor(theme.textColor.opacity(0.6))
                        .lineLimit(2)
                }
            }
            .padding(12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                isSelected
                    ? theme.primaryColor.opacity(0.08)
                    : theme.inputBackgroundColor
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(
                        isSelected
                            ? theme.primaryColor.opacity(0.3)
                            : Color.clear,
                        lineWidth: 1
                    )
            )
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .buttonStyle(.plain)
    }
}
