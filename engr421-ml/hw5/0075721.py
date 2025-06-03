import math
import matplotlib.pyplot as plt
import numpy as np
import scipy.linalg as linalg
import scipy.spatial.distance as dt
import scipy.stats as stats

group_means = np.array([[-5.0, -0.0],
                        [+0.0, +5.0],
                        [+5.0, +0.0],
                        [+0.0, +0.0]])

group_covariances = np.array([[[+0.4, +0.0],
                               [+0.0, +6.0]],
                              [[+6.0, +0.0],
                               [+0.0, +0.4]],
                              [[+0.4, +0.0],
                               [+0.0, +6.0]],
                              [[+6.0, +0.0],
                               [+0.0, +0.4]]])

# read data into memory
data_set = np.genfromtxt("hw05_data_set.csv", delimiter = ",")

# get X values
X = data_set[:, [0, 1]]

# set number of clusters
K = 4

# STEP 2
# should return initial parameter estimates
# as described in the homework description
def initialize_parameters(X, K):
    # your implementation starts below
    N = X.shape[0]
    D = X.shape[1]
    # Load initial centroids from CSV inside the allowed modification area
    initial_centroids = np.genfromtxt("hw05_initial_centroids.csv", delimiter=",")
    # Compute the distance from each point to each initial centroid
    distances = dt.cdist(X, initial_centroids, 'euclidean')
    # Assign each point to the nearest centroid
    assignments = np.argmin(distances, axis=1)

    # Initialize arrays to store means, covariances, and priors
    means = np.zeros((K, D))
    covariances = np.zeros((K, D, D))
    priors = np.zeros(K)

    # Calculate means, covariances, and priors for each cluster
    for k in range(K):
        cluster_points = X[assignments == k]
        means[k, :] = np.mean(cluster_points, axis=0)
        # Ensure there are enough points to calculate covariance
        if cluster_points.shape[0] > 1:
            covariances[k, :, :] = np.cov(cluster_points, rowvar=False)
        else:
            # Provide a small identity matrix if a cluster has 0 or 1 point to avoid singular matrix
            covariances[k, :, :] = np.eye(D) * 1e-5
        priors[k] = cluster_points.shape[0] / float(N)
    # your implementation ends above
    return(means, covariances, priors)

means, covariances, priors = initialize_parameters(X, K)

# STEP 3
# should return final parameter estimates of
# EM clustering algorithm
def em_clustering_algorithm(X, K, means, covariances, priors):
    # your implementation starts below
    N = X.shape[0]  # Number of data points
    D = X.shape[1]  # Dimension of data points

    for iteration in range(100):  # Fixed number of iterations
        # E-Step: Calculate responsibilities
        responsibilities = np.zeros((N, K))
        for k in range(K):
            pdf = stats.multivariate_normal(mean=means[k], cov=covariances[k]).pdf(X)
            responsibilities[:, k] = priors[k] * pdf
        responsibilities /= responsibilities.sum(axis=1, keepdims=True)  # Normalize responsibilities

        # M-Step: Update parameters
        Nk = responsibilities.sum(axis=0)  # Sum of responsibilities for each cluster
        for k in range(K):
            means[k] = np.sum(responsibilities[:, k].reshape(-1, 1) * X, axis=0) / Nk[k]
            diff = X - means[k]
            covariances[k] = np.dot(responsibilities[:, k] * diff.T, diff) / Nk[k]
            covariances[k] += np.eye(D) * 1e-6  # Add regularization to avoid singular covariance matrix
            priors[k] = Nk[k] / N  # Update priors

    assignments = np.argmax(responsibilities, axis=1)  # Assign points to clusters
    # your implementation ends above

    return(means, covariances, priors, assignments)

means, covariances, priors, assignments = em_clustering_algorithm(X, K, means, covariances, priors)
print(means)
print(priors)

# STEP 4
# should draw EM clustering results as described
# in the homework description
def draw_clustering_results(X, K, group_means, group_covariances, means, covariances, assignments):
    # your implementation starts below
    colors = ['r', 'g', 'b', 'c']  # Different colors for each cluster
    
    # Create a figure
    plt.figure(figsize=(10, 10))
    
    # Plot the data points and color them based on cluster assignments
    for k in range(K):
        cluster_points = X[assignments == k]
        plt.scatter(cluster_points[:, 0], cluster_points[:, 1], c=colors[k], label=f'Cluster {k+1}', alpha=0.6)

    # Function to plot Gaussian densities
    def plot_gaussian_contours(mean, cov, color, linestyle, label):
        eigenvalues, eigenvectors = np.linalg.eigh(cov)
        order = eigenvalues.argsort()[::-1]
        eigenvalues, eigenvectors = eigenvalues[order], eigenvectors[:, order]
        
        angle = np.arctan2(*eigenvectors[:, 0][::-1])
        width, height = 2 * np.sqrt(5.991 * eigenvalues)  # Chi-square value for 95% confidence interval
        
        ell = plt.matplotlib.patches.Ellipse(mean, width, height, angle * 180 / np.pi,
                                             edgecolor=color, linestyle=linestyle, linewidth=2, fill=False)
        plt.gca().add_patch(ell)
        plt.scatter(mean[0], mean[1], c=color, s=100, marker='x', linewidths=3, label=label)
    
    # Plot the original Gaussian densities with dashed lines
    for k in range(K):
        plot_gaussian_contours(group_means[k], group_covariances[k], color='black', linestyle='dashed', label=f'Original Gaussian {k+1}')

    # Plot the estimated Gaussian densities with solid lines
    for k in range(K):
        plot_gaussian_contours(means[k], covariances[k], color=colors[k], linestyle='solid', label=f'Estimated Gaussian {k+1}')

    plt.legend()
    plt.title('EM Clustering Results')
    plt.xlabel('X1')
    plt.ylabel('X2')
    plt.grid(True)
    plt.show()
    # your implementation ends above

    
draw_clustering_results(X, K, group_means, group_covariances, means, covariances, assignments)