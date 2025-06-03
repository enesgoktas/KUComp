FROM ubuntu:latest

# Install dependencies
RUN apt-get update && apt-get install -y python3 python3-pip openssh-client ansible

# Add SSH keys
COPY id_rsa /root/.ssh/id_rsa
COPY id_rsa.pub /root/.ssh/id_rsa.pub

# Set permissions for SSH keys
RUN chmod 600 /root/.ssh/id_rsa
RUN chmod 644 /root/.ssh/id_rsa.pub

# Add Ansible files
COPY ansible.cfg /etc/ansible/ansible.cfg
COPY inventory /etc/ansible/hosts
COPY playbook.yml /control-node/playbook.yml

# Keep the container running
CMD ["tail", "-f", "/dev/null"]
