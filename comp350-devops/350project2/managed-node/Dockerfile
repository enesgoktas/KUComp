FROM ubuntu:latest

# Install dependencies
RUN apt-get update && apt-get install -y python3 openssh-server nginx supervisor

# Setup SSH server
RUN mkdir /var/run/sshd
COPY id_rsa.pub /root/.ssh/authorized_keys
RUN chmod 600 /root/.ssh/authorized_keys

# Allow root login with key
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config

# Configure Supervisor
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose SSH and HTTP ports
EXPOSE 22 80

# Start Supervisor
CMD ["/usr/bin/supervisord"]
