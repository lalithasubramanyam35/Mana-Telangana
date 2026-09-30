# 🚀 Deployment Guide - Mana Telangana Application

This application is built with **Streamlit** (Python backend engine). Here are the step-by-step guides for hosting it live on **Streamlit Community Cloud**, **Hugging Face Spaces**, **Render**, and **Docker**.

---

## Method 1: Deploy on Streamlit Community Cloud (Recommended & Free)

Streamlit Community Cloud provides 100% free, native deployment directly from your GitHub repository.

### Steps:
1. **Push your code to GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit for Mana Telangana"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/mana-telangana.git
   git push -u origin main
   ```
2. Go to **[share.streamlit.io](https://share.streamlit.io)** and log in with your GitHub account.
3. Click **"New App"**.
4. Select your repository: `mana-telangana`.
5. Set Main file path: `app.py`.
6. Click **"Deploy!"**.
7. Your app will be live with a shareable URL like `https://mana-telangana.streamlit.app`.

---

## Method 2: Deploy on Hugging Face Spaces (Free)

1. Go to **[huggingface.co/spaces](https://huggingface.co/spaces)**.
2. Click **"Create new Space"**.
3. Choose **SDK**: `Streamlit`.
4. Upload your repository files (`app.py`, `backend.py`, `translations.py`, `prompts.py`, `requirements.txt`, `.streamlit/config.toml`).
5. Your Space will build automatically and display your app live.

---

## Method 3: Deploy on Render.com (Free Web Service)

1. Sign up on **[render.com](https://render.com)**.
2. Click **"New +" -> "Web Service"**.
3. Connect your GitHub repository.
4. Set configuration options:
   - **Environment**: `Python 3`
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `streamlit run app.py --server.address=0.0.0.0 --server.port=$PORT`
5. Click **"Create Web Service"**.

---

## Method 4: Deploy using Docker

To build and run on any cloud provider or server with Docker:

```bash
docker build -t mana-telangana .
docker run -p 8501:8501 mana-telangana
```

Then visit `http://localhost:8501`.
