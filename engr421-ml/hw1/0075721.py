import numpy as np
import pandas as pd



X = np.genfromtxt("hw01_data_points.csv", delimiter = ",", dtype = str)
y = np.genfromtxt("hw01_class_labels.csv", delimiter = ",", dtype = int)



# STEP 3
# first 50000 data points should be included to train
# remaining 43925 data points should be included to test
# should return X_train, y_train, X_test, and y_test
def train_test_split(X, y):
    # your implementation starts below
    # Select the first 50000 data points for training
    X_train = X[:50000]
    y_train = y[:50000]
    
    # Select the remaining 43925 data points for testing
    X_test = X[50000:]
    y_test = y[50000:]
    # your implementation ends above
    return(X_train, y_train, X_test, y_test)

X_train, y_train, X_test, y_test = train_test_split(X, y)
print(X_train.shape)
print(y_train.shape)
print(X_test.shape)
print(y_test.shape)



# STEP 4
# assuming that there are K classes
# should return a numpy array with shape (K,)
def estimate_prior_probabilities(y):
    # your implementation starts below
    # Count the occurrences of each class
    class_counts = np.bincount(y)
    
    # Calculate the total number of data points
    total_samples = len(y)
    
    # Calculate the prior probability estimates
    class_priors = class_counts / total_samples
    # your implementation ends above
    return(class_priors)

class_priors = estimate_prior_probabilities(y_train)
print(class_priors)



# STEP 5
# assuming that there are K classes and D features
# should return four numpy arrays with shape (K, D)
def estimate_nucleotide_probabilities(X, y):
    # your implementation starts below
    num_classes = len(np.unique(y_train))  # Get the number of unique classes
    num_positions = X_train.shape[1]  # Get the number of positions in the sequences

    # Initialize arrays to store probabilities
    pAcd = np.zeros((num_classes, num_positions))
    pCcd = np.zeros((num_classes, num_positions))
    pGcd = np.zeros((num_classes, num_positions))
    pTcd = np.zeros((num_classes, num_positions))

    # Calculate probabilities for each class and position
    for c in range(num_classes):
        # Select data points belonging to class c
        X_class_c = X_train[y_train == c + 1]  # Assuming class labels start from 1

        # Count occurrences of each nucleotide at each position
        total_sequences = len(X_class_c)
        total_nucleotides = total_sequences * num_positions

        # Calculate conditional probabilities for each nucleotide at each position
        pAcd[c] = (np.sum(X_class_c == 'A', axis=0) + 1) / (total_sequences + 4)  # Laplace smoothing
        pCcd[c] = (np.sum(X_class_c == 'C', axis=0) + 1) / (total_sequences + 4)
        pGcd[c] = (np.sum(X_class_c == 'G', axis=0) + 1) / (total_sequences + 4)
        pTcd[c] = (np.sum(X_class_c == 'T', axis=0) + 1) / (total_sequences + 4)
    # your implementation ends above
    return(pAcd, pCcd, pGcd, pTcd)

pAcd, pCcd, pGcd, pTcd = estimate_nucleotide_probabilities(X_train, y_train)
print(pAcd)
print(pCcd)
print(pGcd)
print(pTcd)



# STEP 6
# assuming that there are N data points and K classes
# should return a numpy array with shape (N, K)
def calculate_score_values(X, pAcd, pCcd, pGcd, pTcd, class_priors):
    # your implementation starts below
    num_classes, num_positions = pAcd.shape
    num_samples = X.shape[0]
    
    score_values = np.zeros((num_samples, num_classes))
    
    for i in range(num_samples):
        for c in range(num_classes):
            # Start with the logarithm of the prior probability of class c
            score = np.log(class_priors[c] + np.finfo(float).eps)
            
            # Add the logarithm of the conditional probability of observing each nucleotide given class c
            score += np.sum(np.log([pAcd[c, j] if X[i, j] == 'A' else
                                     pCcd[c, j] if X[i, j] == 'C' else
                                     pGcd[c, j] if X[i, j] == 'G' else
                                     pTcd[c, j] for j in range(num_positions)]))
            
            score_values[i, c] = score
    
    # your implementation ends above
    return(score_values)

scores_train = calculate_score_values(X_train, pAcd, pCcd, pGcd, pTcd, class_priors)
print(scores_train)

scores_test = calculate_score_values(X_test, pAcd, pCcd, pGcd, pTcd, class_priors)
print(scores_test)



# STEP 7
# assuming that there are K classes
# should return a numpy array with shape (K, K)
def calculate_confusion_matrix(y_truth, score_values):
    # your implementation starts below
    num_samples, num_classes = score_values.shape
    y_pred = np.argmax(score_values, axis=1) + 1  # Adding 1 to match class labels (assuming class labels start from 1)
    
    # Initialize confusion matrix
    confusion_matrix = np.zeros((num_classes, num_classes), dtype=int)
    
    for i in range(num_samples):
        true_class = y_truth[i]
        predicted_class = y_pred[i]
        
        # Increment the count in the confusion matrix
        confusion_matrix[true_class - 1, predicted_class - 1] += 1
    # your implementation ends above
    return(confusion_matrix)

confusion_train = calculate_confusion_matrix(y_train, scores_train)
print(confusion_train)

confusion_test = calculate_confusion_matrix(y_test, scores_test)
print(confusion_test)
