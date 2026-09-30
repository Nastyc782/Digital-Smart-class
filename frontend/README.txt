# Digital Smart Class - Frontend

React + Vite application with Vercel Speed Insights configured.

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```
   The app will be available at http://localhost:3000

3. Build for production:
   ```bash
   npm run build
   ```

4. Preview production build:
   ```bash
   npm run preview
   ```

## Features

- ⚡ Vite for fast development
- ⚛️ React 19
- 📊 Vercel Speed Insights pre-configured
- 🔄 Proxy configured for Spring Boot backend (port 8080)

## Project Structure

- `src/` - Source files
  - `main.jsx` - Entry point
  - `App.jsx` - Main App component with Speed Insights
  - `App.css` - App styles
  - `index.css` - Global styles
- `index.html` - HTML template
- `vite.config.js` - Vite configuration

## Speed Insights

Vercel Speed Insights is already configured in `App.jsx`:

```jsx
import { SpeedInsights } from '@vercel/speed-insights/react'

function App() {
  return (
    <>
      {/* Your app content */}
      <SpeedInsights />
    </>
  )
}
```

This will automatically track Core Web Vitals and send data to Vercel when deployed.
