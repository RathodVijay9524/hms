# Kubernetes Deployment Guide for User Master Application

This guide explains how to deploy the User Master application to a Kubernetes cluster using Minikube.

## Prerequisites

1. Docker Desktop installed and running
2. Minikube installed
3. kubectl configured to work with Minikube

## Deployment Steps

### 1. Start Minikube

```bash
# Start Minikube with Docker driver
minikube start --driver=docker --cpus=2 --memory=4096

# Enable ingress addon (if needed)
minikube addons enable ingress

# Set Docker environment to use Minikube's Docker daemon
eval $(minikube docker-env)
```

### 2. Build and Load Docker Image

```bash
# Build the application JAR
./gradlew clean build

# Build the Docker image
docker build -t user-master:1.0 .

# Load the image into Minikube
minikube image load user-master:1.0
```

### 3. Deploy MySQL and Application

```bash
# Create namespace (optional)
kubectl create namespace user-master

# Deploy MySQL and Application
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/deployment.yaml
```

### 4. Verify Deployment

```bash
# Check pods
kubectl get pods -n user-master

# Check services
kubectl get svc -n user-master

# Get application URL
minikube service user-master-service --url -n user-master
```

### 5. Access the Application

The application will be available at the URL provided by the `minikube service` command. By default, it will be accessible at:

```
http://<minikube-ip>:30007
```

### 6. Useful Commands

```bash
# View logs
kubectl logs -f deployment/user-master -n user-master

# Access MySQL
kubectl run -it --rm --image=mysql:8.0 --restart=Never mysql-client -- mysql -h mysql-service -u root -p

# Delete all resources
kubectl delete -f k8s/ --recursive
```

## Configuration

You can customize the deployment by modifying the following environment variables in `deployment.yaml`:

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Troubleshooting

1. If the application fails to start, check the logs:
   ```bash
   kubectl logs -f deployment/user-master -n user-master
   ```

2. If MySQL connection fails, verify the MySQL pod is running:
   ```bash
   kubectl get pods -n user-master | grep mysql
   ```

3. To access the MySQL database directly:
   ```bash
   kubectl port-forward svc/mysql-service 3306:3306 -n user-master
   ```
   Then connect using your MySQL client at `localhost:3306`
