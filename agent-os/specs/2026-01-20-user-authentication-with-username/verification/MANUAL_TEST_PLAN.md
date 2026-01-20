# Manual Test Plan - User Authentication

## Prerequisites
- Application running at http://localhost:8080
- Start with: `cd /Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single && ./mvnw quarkus:dev`

## Test Scenarios

### Test 1: Initial Load
**Steps:**
1. Open browser to http://localhost:8080
2. Verify redirect to http://localhost:8080/app.html

**Expected:**
- Login screen displays
- Banner shows: "Welcome to Red Hat One Las Vegas Trivia"
- Username input field is empty
- "Start Playing" button is disabled
- No error messages shown

**Screenshot:** `01-initial-load.png`

---

### Test 2: Empty Username Validation
**Steps:**
1. Click in username field
2. Click out of username field (without typing)

**Expected:**
- No error message displayed
- Button remains disabled
- No visual error state on input

**Screenshot:** `02-empty-field.png`

---

### Test 3: Too Short Username
**Steps:**
1. Type "A" in username field
2. Observe validation feedback

**Expected:**
- Error message: "Username must be between 2 and 30 characters"
- Input has red border
- Button remains disabled

**Screenshot:** `03-too-short.png`

---

### Test 4: Invalid Characters
**Steps:**
1. Clear field
2. Type "John@Doe" in username field

**Expected:**
- Error message: "Username can only contain letters, numbers, and spaces"
- Input has red border
- Button remains disabled

**Screenshot:** `04-invalid-chars.png`

---

### Test 5: Too Long Username
**Steps:**
1. Clear field
2. Type "ThisIsAVeryLongUsernameThatExceedsTheMaximumAllowedLength" (>30 chars)

**Expected:**
- Error message: "Username must be between 2 and 30 characters"
- Input has red border
- Button remains disabled

**Screenshot:** `05-too-long.png`

---

### Test 6: Offensive Language (if configured)
**Steps:**
1. Clear field
2. Type "badword1" in username field

**Expected:**
- Error message: "Please choose a different username"
- Input has red border
- Button remains disabled

**Screenshot:** `06-offensive-language.png`

---

### Test 7: Valid Username
**Steps:**
1. Clear field
2. Type "John Doe" in username field

**Expected:**
- No error message
- Input has green border (or no error styling)
- Button becomes enabled
- Button changes appearance (not grayed out)

**Screenshot:** `07-valid-username.png`

---

### Test 8: Error Clearing
**Steps:**
1. Type "A" (triggers error)
2. Type "AB" (valid length, only letters)

**Expected:**
- Error message disappears
- Input border changes from red to green
- Button becomes enabled

**Screenshot:** `08-error-cleared.png`

---

### Test 9: Multi-word Username
**Steps:**
1. Clear field
2. Type "Sarah Jane Smith" in username field

**Expected:**
- No error message
- Input has green border
- Button is enabled
- Spaces are accepted

**Screenshot:** `09-multi-word.png`

---

### Test 10: Successful Login - Welcome Screen
**Steps:**
1. Type "John Doe" in username field
2. Click "Start Playing" button

**Expected:**
- Login screen disappears
- Welcome screen appears with fade-in animation
- Message shows: "Welcome, John Doe! Let's play!"
- Message displays for approximately 2.5 seconds

**Screenshot:** `10-welcome-screen.png`

---

### Test 11: Transition to Game Screen
**Steps:**
1. Continue from Test 10
2. Wait 2.5 seconds

**Expected:**
- Welcome screen disappears
- Game screen appears
- Message shows: "Loading..."

**Screenshot:** `11-game-screen.png`

---

### Test 12: SessionStorage Persistence
**Steps:**
1. Open browser DevTools → Application → Session Storage
2. Look for "vegas-trivia-username" key

**Expected:**
- Key exists with value "John Doe"
- Value matches the username entered

**Screenshot:** `12-session-storage.png`

---

### Test 13: Auto-Login (Refresh)
**Steps:**
1. From game screen (with active session)
2. Refresh the page (F5 or Cmd+R)

**Expected:**
- Login screen does NOT appear
- Game screen appears immediately
- "Loading..." message visible
- No welcome message shown

**Screenshot:** `13-auto-login-refresh.png`

---

### Test 14: Session Cleared (New Tab)
**Steps:**
1. Close the browser tab
2. Open new browser tab to http://localhost:8080

**Expected:**
- Login screen appears
- No auto-login
- SessionStorage is empty
- Must enter username again

**Screenshot:** `14-new-tab-login.png`

---

### Test 15: Responsive - Mobile (320px)
**Steps:**
1. Resize browser to 320px width (or use DevTools device emulation)
2. Load login screen

**Expected:**
- Layout remains usable
- Banner text is readable (may wrap)
- Input field fits width
- Button fits width
- No horizontal scroll

**Screenshot:** `15-mobile-320px.png`

---

### Test 16: Responsive - Tablet (768px)
**Steps:**
1. Resize browser to 768px width
2. Load login screen

**Expected:**
- Layout adapts appropriately
- All elements visible and properly spaced

**Screenshot:** `16-tablet-768px.png`

---

### Test 17: Responsive - Desktop (1024px+)
**Steps:**
1. Resize browser to 1024px or larger
2. Load login screen

**Expected:**
- Layout is centered
- Max-width container keeps content readable
- Proper spacing and proportions

**Screenshot:** `17-desktop-1024px.png`

---

### Test 18: Keyboard Navigation
**Steps:**
1. Load login screen
2. Press Tab key to navigate
3. Try to submit using Enter key in username field

**Expected:**
- Tab moves focus to username field
- Tab moves focus to button (if enabled)
- Enter in field submits form (if valid)
- Focus indicators are visible

**Screenshot:** `18-keyboard-focus.png`

---

### Test 19: Accessibility - ARIA
**Steps:**
1. Open browser DevTools → Accessibility panel
2. Inspect username input
3. Inspect error message

**Expected:**
- Input has aria-describedby pointing to error element
- Error element has role="alert" and aria-live="polite"
- All interactive elements have accessible names

**Screenshot:** `19-accessibility-aria.png`

---

### Test 20: Real-time Validation
**Steps:**
1. Type slowly in username field: "J" → "Jo" → "Joh" → "John"
2. Observe validation updates

**Expected:**
- "J" → Error shown
- "Jo" → Error clears, button enables
- "Joh" → Still valid
- "John" → Still valid
- Validation happens on each keystroke

**Screenshot:** `20-realtime-validation.png`

---

## Summary Checklist

After completing all tests, verify:

- [ ] Login screen displays correctly on all screen sizes
- [ ] All validation rules work as expected
- [ ] Error messages are clear and specific
- [ ] Button enable/disable state works correctly
- [ ] Visual feedback (red/green borders) works
- [ ] Welcome screen appears with correct message
- [ ] Welcome screen displays for 2-3 seconds
- [ ] Game screen appears after welcome timeout
- [ ] SessionStorage stores username correctly
- [ ] Auto-login works on page refresh
- [ ] Session clears on new tab/window
- [ ] Keyboard navigation works properly
- [ ] ARIA attributes are present and correct
- [ ] Responsive design works on mobile, tablet, desktop
- [ ] No console errors in browser DevTools

## Browser Testing Matrix

Test on:
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Safari (latest)
- [ ] Edge (latest)

## Performance Checks

- [ ] Page loads in < 2 seconds
- [ ] Bundle size is reasonable (< 100KB)
- [ ] No memory leaks on repeated use
- [ ] Animations are smooth (60fps)

## Accessibility Checks

- [ ] Can complete entire flow with keyboard only
- [ ] Screen reader announces errors and state changes
- [ ] Color contrast meets WCAG AA standards
- [ ] Focus indicators are visible
