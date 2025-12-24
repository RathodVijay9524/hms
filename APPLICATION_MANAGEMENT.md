# User Master Application - Start/Stop Management Guide

## 📋 Overview
This document provides step-by-step instructions for starting, stopping, and managing your Spring Boot User Master application deployed on Kubernetes with Cloudflare tunnel.

## 🚀 Starting the Application

### Prerequisites
- Minikube installed and running
- Docker installed
- kubectl configured
- Cloudflare tunnel configured

### Step 1: Start Minikube
```bash
# Start Minikube cluster
minikube start

# Verify Minikube is running
minikube status
```

### Step 2: Deploy MySQL Database
```bash
# Apply MySQL deployment (if not already running)
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/mysql-service.yaml

# Verify MySQL is running
kubectl get pods | grep mysql
```

### Step 3: Build and Deploy Application
```bash
# Build the application with Gradle
./gradlew clean build

# Build Docker image
docker build -t user-master:latest .

# Load image into Minikube
minikube image load user-master:latest

# Deploy application to Kubernetes
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### Step 4: Verify Deployment
```bash
# Check all pods are running
kubectl get pods

# Expected output:
# NAME                                      READY   STATUS    RESTARTS   AGE
# mysql-75bd5cfcfc-hg52s                    1/1     Running   0          XXm
# user-master-deployment-6f79586c9c-xxxxx   1/1     Running   0          XXm
# user-master-deployment-6f79586c9c-xxxxx   1/1     Running   0          XXm
# user-master-deployment-6f79586c9c-xxxxx   1/1     Running   0          XXm

# Check services
kubectl get services
```

### Step 5: Set Up Port Forwarding
```bash
# Forward Kubernetes service to localhost for Cloudflare tunnel
kubectl port-forward service/user-master-service 9091:9091
```

### Step 6: Start Cloudflare Tunnel
```bash
# Start the Cloudflare tunnel (in separate terminal)
cloudflared tunnel run vijay-local
```

### Step 7: Verify Application is Accessible
```bash
# Test welcome endpoint
curl https://api.codewithvijay.online/api/v1/home/welcome

# Expected response: "Welcome to java Programing"
```

## 🛑 Stopping the Application

### Step 1: Stop Cloudflare Tunnel
```bash
# Stop the cloudflared process (Ctrl+C in the tunnel terminal)
# Or kill the process
taskkill /f /im cloudflared.exe
```

### Step 2: Stop Port Forwarding
```bash
# Stop kubectl port-forward (Ctrl+C in the port-forward terminal)
# Or kill kubectl processes
taskkill /f /im kubectl.exe
```

### Step 3: Stop Application Pods
```bash
# Scale down the application to 0 replicas
kubectl scale deployment user-master-deployment --replicas=0

# Or delete the deployment entirely
kubectl delete -f k8s/deployment.yaml
kubectl delete -f k8s/service.yaml
```

### Step 4: Stop MySQL (Optional)
```bash
# If you want to stop the database as well
kubectl delete -f k8s/mysql-deployment.yaml
kubectl delete -f k8s/mysql-service.yaml

# WARNING: This will delete all data unless you have persistent volumes
```

### Step 5: Stop Minikube (Optional)
```bash
# Stop the entire Minikube cluster
minikube stop

# Or delete the cluster entirely (WARNING: This deletes everything)
minikube delete
```

## 🔄 Restart/Scale Operations

### Restart Application (Without Data Loss)
```bash
# Restart all pods
kubectl rollout restart deployment/user-master-deployment

# Check rollout status
kubectl rollout status deployment/user-master-deployment
```

### Scale Application Up/Down
```bash
# Scale to 3 replicas (for better performance)
kubectl scale deployment user-master-deployment --replicas=3

# Scale to 1 replica (to save resources)
kubectl scale deployment user-master-deployment --replicas=1

# Scale to 0 replicas (stop without deleting)
kubectl scale deployment user-master-deployment --replicas=0
```

## 📊 Monitoring and Troubleshooting

### Check Application Status
```bash
# View all pods
kubectl get pods

# View pod details
kubectl describe pod <pod-name>

# View application logs
kubectl logs -f deployment/user-master-deployment

# View MySQL logs
kubectl logs -f deployment/mysql
```

### Check Services and Networking
```bash
# View all services
kubectl get services

# Check service endpoints
kubectl get endpoints

# Test internal connectivity
kubectl exec -it <pod-name> -- curl http://mysql-service:3306
```

### Database Access
```bash
# Access MySQL database
kubectl exec -it <mysql-pod-name> -- mysql -u root -ppassword user_master

# View users
kubectl exec <mysql-pod-name> -- mysql -u root -ppassword -e "USE user_master; SELECT id, username, email FROM users;"
```

## 🔧 Configuration Files

### Key Files Location
- **Deployment**: `k8s/deployment.yaml`
- **Service**: `k8s/service.yaml`
- **Dockerfile**: `Dockerfile`
- **Build**: `build.gradle`
- **Application Properties**: `src/main/resources/application.properties`

### Important Endpoints
- **Public URL**: https://api.codewithvijay.online
- **Health Check**: https://api.codewithvijay.online/api/v1/home/welcome
- **Login**: https://api.codewithvijay.online/api/auth/login
- **Admin Endpoints**: https://api.codewithvijay.online/api/roles (requires admin role)

## 🚨 Emergency Procedures

### Application Not Responding
```bash
# Check pod status
kubectl get pods

# Restart deployment
kubectl rollout restart deployment/user-master-deployment

# Check logs for errors
kubectl logs -f deployment/user-master-deployment
```

### Database Connection Issues
```bash
# Check MySQL pod
kubectl get pods | grep mysql

# Restart MySQL
kubectl rollout restart deployment/mysql

# Verify database connectivity
kubectl exec -it <app-pod> -- nc -zv mysql-service 3306
```

### Cloudflare Tunnel Issues
```bash
# Check tunnel status
cloudflared tunnel info vijay-local

# Restart tunnel
cloudflared tunnel run vijay-local

# Check port forwarding
netstat -an | findstr 9091
```

## 📝 Notes

- **Data Persistence**: MySQL data is stored in the pod. For production, use persistent volumes.
- **Scaling**: Current setup supports horizontal scaling (multiple pods).
- **Security**: Database password is hardcoded. Use Kubernetes secrets for production.
- **Monitoring**: Consider adding health checks and monitoring tools for production use.

## 🎯 Quick Commands Summary

### Start Everything
```bash
minikube start
kubectl apply -f k8s/
kubectl port-forward service/user-master-service 9091:9091 &
cloudflared tunnel run vijay-local
```

### Stop Everything
```bash
kubectl scale deployment user-master-deployment --replicas=0
kubectl scale deployment mysql --replicas=0
minikube stop
```

### Check Status
```bash
kubectl get pods
kubectl get services
curl https://api.codewithvijay.online/api/v1/home/welcome
```

---

**Last Updated**: August 24, 2025  
**Application Version**: 0.0.1-SNAPSHOT  
**Kubernetes**: Minikube  
**Database**: MySQL 8.0  
**Public Access**: Cloudflare Tunnel
