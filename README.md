# MWS Care Android App

A delivery management application for healthcare providers that allows scanning and assigning
parcels to delivery riders.

## Features

### Home Screen

- **Dashboard Statistics**: Shows total parcels today, assigned parcels, unassigned parcels, and
  active riders
- **Rider List**: Displays all riders with their zones, order counts, and online status
- **Scanner FAB**: Floating action button to access the scanner functionality

### Scanner Screen

- **Camera Interface**: Allows users to capture images of prescription packages
- **Scan Frame**: Visual guide for proper parcel positioning
- **Action Controls**: Upload/capture and flashlight toggle buttons

### Details Screen

- **Scanned Image Preview**: Shows the captured parcel image
- **Available Riders**: Lists riders available for the scanned parcel's delivery area
- **Assignment Controls**: Assign or cancel buttons for each rider
- **Delivery Address**: Shows parsed delivery information from the scanned image
- **Zone Information**: Displays area and sector details

## Technical Structure

### Data Models

- `Rider`: Represents delivery rider information
- `RiderStatus`: Enum for rider availability states
- `ParcelInfo`: Contains scanned parcel and delivery details

### UI Components

- `MainActivity`: Host activity with fragment management
- `HomeFragment`: Dashboard and rider list display
- `ScannerFragment`: Camera interface and image capture
- `DetailsFragment`: Scan results and rider assignment
- `RiderAdapter`: RecyclerView adapter for rider list display

### Navigation Flow

1. **Home** → Scanner (via FAB)
2. **Scanner** → Details (after image capture)
3. **Details** → Home (after assignment or back navigation)

## Setup Requirements

- Android SDK 28+
- Kotlin support
- Camera permissions for scanning functionality

## UI Design

The app follows Material Design principles with:

- Blue primary color scheme (#3F51B5)
- Card-based layout structure
- Status-based color coding for riders
- Responsive table layouts
- Smooth screen transitions

## Current Status

Basic UI structure and navigation implemented. Ready for enhanced camera functionality and backend
integration for real parcel scanning and rider management.