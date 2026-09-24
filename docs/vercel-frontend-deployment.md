# NexusHR Frontend Deployment on Vercel

## Overview
NexusHR frontend can be deployed seamlessly as a static Single Page Application (SPA) on Vercel.

---

## Deployment Steps

1. **Push to GitHub**:
   Ensure your repository is pushed to GitHub (e.g., `github.com/your-username/nexushr`).

2. **Import Project in Vercel**:
   - Log in to [Vercel Dashboard](https://vercel.com).
   - Click **Add New...** -> **Project**.
   - Import your NexusHR GitHub repository.

3. **Configure Project Settings**:
   Vercel automatically detects Vite. Verify the build settings:
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm install`

4. **Environment Variables**:
   In your Vercel Project Settings -> **Environment Variables**, add:
   - `VITE_API_BASE_URL`: `https://api.nexushr.com/api` (or your deployed Spring Boot API Gateway URL)

5. **Deploy**:
   Click **Deploy**. Vercel will build and deploy your React frontend instantly with global CDN caching and automatic SPA fallback routing configured via `vercel.json`.
