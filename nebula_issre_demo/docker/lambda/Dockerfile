# Use the official AWS Lambda Python base image
FROM public.ecr.aws/lambda/python:3.10

# Set the working directory
WORKDIR ${LAMBDA_TASK_ROOT}

# Update and install necessary packages
#RUN yum update -y && \
#    yum install -y ffmpeg libsm6 libxext6 git

# Install Python dependencies
# Copy the requirements file into the container
COPY requirements.txt .

# Install the dependencies
RUN pip install --no-cache-dir -r requirements.txt

COPY nebula-cf706-firebase-adminsdk-cjswy-6d96607db5.json ${LAMBDA_TASK_ROOT}/nebula-cf706-firebase-adminsdk-cjswy-6d96607db5.json

COPY . .

# Copy the Lambda function code
COPY lambda.py ${LAMBDA_TASK_ROOT}/lambda.py

# Set the CMD to your Lambda function handler
CMD [ "lambda.handler" ]