import matplotlib.pyplot as plt
import numpy as np


# read data into memory
data_set_train = np.genfromtxt("hw04_data_set_train.csv", delimiter = ",")
data_set_test = np.genfromtxt("hw04_data_set_test.csv", delimiter = ",")

# get X and y values
X_train = data_set_train[:, 0:1]
y_train = data_set_train[:, 1]
X_test = data_set_test[:, 0:1]
y_test = data_set_test[:, 1]

# set drawing parameters
minimum_value = 0.00
maximum_value = 2.00
step_size = 0.002
X_interval = np.arange(start = minimum_value, stop = maximum_value + step_size, step = step_size)
X_interval = X_interval.reshape(len(X_interval), 1)

def plot_figure(X_train, y_train, X_test, y_test, X_interval, y_interval_hat):
    fig = plt.figure(figsize = (8, 4))
    plt.plot(X_train[:, 0], y_train, "b.", markersize = 10)
    plt.plot(X_test[:, 0], y_test, "r.", markersize = 10)
    plt.plot(X_interval[:, 0], y_interval_hat, "k-")
    plt.xlim([-0.05, 2.05])
    plt.xlabel("Time (sec)")
    plt.ylabel("Signal (millivolt)")
    plt.legend(["training", "test"])
    plt.show()
    return(fig)

# STEP 2
# should return necessary data structures for trained tree
def decision_tree_regression_train(X_train, y_train, P):
    # create necessary data structures
    node_indices = {}
    is_terminal = {}
    need_split = {}

    node_features = {}
    node_splits = {}
    node_means = {}
    # your implementation starts below
    def build_tree(node_index, data_indices):
        node_indices[node_index] = data_indices
        # Check if number of data points at this node is less than or equal to P
        if len(data_indices) <= P:
            is_terminal[node_index] = True
            node_means[node_index] = np.mean(y_train[data_indices])
        else:
            is_terminal[node_index] = False
            # Find best split
            best_feature = None
            best_split = None
            best_loss = float('inf')
            for feature_index in range(X_train.shape[1]):
                feature_values = X_train[data_indices, feature_index]
                for split_value in np.unique(feature_values):
                    left_indices = data_indices[feature_values <= split_value]
                    right_indices = data_indices[feature_values > split_value]
                    if len(left_indices) == 0 or len(right_indices) == 0:
                        continue
                    # Calculate mean squared error
                    left_mean = np.mean(y_train[left_indices])
                    right_mean = np.mean(y_train[right_indices])
                    loss = np.sum((y_train[left_indices] - left_mean) ** 2) + np.sum((y_train[right_indices] - right_mean) ** 2)
                    if loss < best_loss:
                        best_loss = loss
                        best_feature = feature_index
                        best_split = split_value
            # Store best split
            node_features[node_index] = best_feature
            node_splits[node_index] = best_split
            # Recursively build left and right child nodes
            left_indices = data_indices[X_train[data_indices, best_feature] <= best_split]
            right_indices = data_indices[X_train[data_indices, best_feature] > best_split]
            build_tree(2 * node_index + 1, left_indices)
            build_tree(2 * node_index + 2, right_indices)

    # Start building the tree from root node
    build_tree(0, np.arange(len(y_train)))

    # your implementation ends above
    return(is_terminal, node_features, node_splits, node_means)

# STEP 3
# assuming that there are N query data points
# should return a numpy array with shape (N,)
def decision_tree_regression_test(X_query, is_terminal, node_features, node_splits, node_means):
    # your implementation starts below
    y_hat = np.zeros(X_query.shape[0])
    
    # Function to recursively traverse the tree and make predictions
    def traverse_tree(node_index, query_point_index):
        # If the node is terminal, return the mean value stored at that node
        if is_terminal[node_index]:
            return node_means[node_index]
        else:
            # Get the feature and split value for the current node
            feature_index = node_features[node_index]
            split_value = node_splits[node_index]
            # Check if the query point belongs to the left or right child node
            if X_query[query_point_index, feature_index] <= split_value:
                return traverse_tree(2 * node_index + 1, query_point_index)
            else:
                return traverse_tree(2 * node_index + 2, query_point_index)
    
    # Traverse the tree for each query data point to make predictions
    for i in range(X_query.shape[0]):
        y_hat[i] = traverse_tree(0, i)
    # your implementation ends above
    return(y_hat)

# STEP 4
# assuming that there are T terminal node
# should print T rule sets as described
def extract_rule_sets(is_terminal, node_features, node_splits, node_means):
    # your implementation starts below
    def traverse_tree(node_index, rule):
        # If the node is terminal, print the rule
        if is_terminal[node_index]:
            print(f"Rule {node_index}: {rule} => Predicted value: {node_means[node_index]}")
        else:
            # Get the feature and split value for the current node
            feature_index = node_features[node_index]
            split_value = node_splits[node_index]
            # Generate rule for the left child node
            left_rule = f"X[{feature_index}] <= {split_value}"
            traverse_tree(2 * node_index + 1, rule + " and " + left_rule)
            # Generate rule for the right child node
            right_rule = f"X[{feature_index}] > {split_value}"
            traverse_tree(2 * node_index + 2, rule + " and " + right_rule)

    # Start traversal from the root node (index 0)
    traverse_tree(0, "Root")
    # your implementation ends above

P = 20
is_terminal, node_features, node_splits, node_means = decision_tree_regression_train(X_train, y_train, P)
y_interval_hat = decision_tree_regression_test(X_interval, is_terminal, node_features, node_splits, node_means)
fig = plot_figure(X_train, y_train, X_test, y_test, X_interval, y_interval_hat)
fig.savefig("decision_tree_regression_{}.pdf".format(P), bbox_inches = "tight")

y_train_hat = decision_tree_regression_test(X_train, is_terminal, node_features, node_splits, node_means)
rmse = np.sqrt(np.mean((y_train - y_train_hat)**2))
print("RMSE on training set is {} when P is {}".format(rmse, P))

y_test_hat = decision_tree_regression_test(X_test, is_terminal, node_features, node_splits, node_means)
rmse = np.sqrt(np.mean((y_test - y_test_hat)**2))
print("RMSE on test set is {} when P is {}".format(rmse, P))

P = 50
is_terminal, node_features, node_splits, node_means = decision_tree_regression_train(X_train, y_train, P)
y_interval_hat = decision_tree_regression_test(X_interval, is_terminal, node_features, node_splits, node_means)
fig = plot_figure(X_train, y_train, X_test, y_test, X_interval, y_interval_hat)
fig.savefig("decision_tree_regression_{}.pdf".format(P), bbox_inches = "tight")

y_train_hat = decision_tree_regression_test(X_train, is_terminal, node_features, node_splits, node_means)
rmse = np.sqrt(np.mean((y_train - y_train_hat)**2))
print("RMSE on training set is {} when P is {}".format(rmse, P))

y_test_hat = decision_tree_regression_test(X_test, is_terminal, node_features, node_splits, node_means)
rmse = np.sqrt(np.mean((y_test - y_test_hat)**2))
print("RMSE on test set is {} when P is {}".format(rmse, P))

extract_rule_sets(is_terminal, node_features, node_splits, node_means)
