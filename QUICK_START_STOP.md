# 🚀 Quick Start/Stop Guide - User Master Application

## ⚡ Quick Start (Everything)
```bash
# 1. Start Minikube
minikube start

# 2. Deploy application
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# 3. Set up port forwarding (keep this terminal open)
kubectl port-forward service/user-master-service 9091:9091

# 4. Start Cloudflare tunnel (in another terminal)
cloudflared tunnel run vijay-local
```

## ⚡ Quick Stop (Everything)
```bash
# 1. Stop Cloudflare tunnel (Ctrl+C or)
taskkill /f /im cloudflared.exe

# 2. Stop port forwarding (Ctrl+C or)
taskkill /f /im kubectl.exe

# 3. Scale down application
kubectl scale deployment user-master-deployment --replicas=0

# 4. Stop Minikube (optional)
minikube stop
```

## 🔄 Quick Restart (App Only)
```bash
# Restart application pods
kubectl rollout restart deployment/user-master-deployment

# Check status
kubectl get pods
```

## 📊 Quick Status Check
```bash
# Check all pods
kubectl get pods

# Test application
curl https://api.codewithvijay.online/api/v1/home/welcome
```

## 🎯 Scale Performance
```bash
# Scale up for better performance (3 pods)
kubectl scale deployment user-master-deployment --replicas=3

# Scale down to save resources (1 pod)
kubectl scale deployment user-master-deployment --replicas=1
```

## 🔧 Current Configuration
- **Public URL**: https://api.codewithvijay.online
- **Database**: MySQL (user_master)
- **Admin User**: omvijay44@gmail.com (ROLE_ADMIN)
- **Pods**: 3 replicas for high performance
- **Port**: 9091 (internal), 443 (external via Cloudflare)

---
*For detailed instructions, see APPLICATION_MANAGEMENT.md*
