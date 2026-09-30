import { SpeedInsights } from '@vercel/speed-insights/react'
import './App.css'

function App() {
  return (
    <>
      <div className="app">
        <header>
          <h1>Digital Smart Class</h1>
          <p>E-learning platform - Coming soon</p>
        </header>
        <main>
          <div className="hero">
            <h2>Welcome to Digital Smart Class</h2>
            <p>
              An innovative e-learning platform connecting students and teachers.
            </p>
            <div className="features">
              <div className="feature">
                <h3>📚 Course Management</h3>
                <p>Comprehensive course catalog with video lessons and materials</p>
              </div>
              <div className="feature">
                <h3>💳 Secure Payments</h3>
                <p>MTN Mobile Money integration for seamless enrollment</p>
              </div>
              <div className="feature">
                <h3>📊 Progress Tracking</h3>
                <p>Monitor student progress and course completion</p>
              </div>
              <div className="feature">
                <h3>👥 Role-based Access</h3>
                <p>Student, Teacher, Admin, and Accountant dashboards</p>
              </div>
            </div>
          </div>
        </main>
      </div>
      <SpeedInsights />
    </>
  )
}

export default App
