import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

import io
import requests
import numpy as np
from tensorflow.keras.preprocessing import image
from PIL import Image
import tensorflow as tf
from flask import Flask, request, jsonify

app = Flask(__name__)

# Load the model
model = tf.keras.models.load_model('fish_disease_model.h5')

# Define class labels
class_labels = {
    0: 'Bacterial Gill Disease',
    1: 'Cotton Wool',
    2: 'Fin Root',
    3: 'Healthy Fish',
    4: 'White Spot'
}

@app.route('/predict', methods=['POST'])
def predict():
    try:
        

        # Get the image URL from the request
        image_url = request.json.get('image_url')
        print("Received URL:", image_url)  
        if not image_url:
            return jsonify({'error': 'Image URL not provided'})

        # Fetch the image from the provided URL
        response = requests.get(image_url)
        if response.status_code != 200:
            return jsonify({'error': 'Failed to fetch image from the URL'})

        # Load the image using PIL
        img = Image.open(io.BytesIO(response.content)).convert('RGB')
        img = img.resize((224, 224))  # Resize the image to match the model's input size

        # Convert the image to an array and preprocess it
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array /= 255.0

        # Make prediction
        predictions = model.predict(img_array)
        predicted_class = np.argmax(predictions)
        prediction_label = class_labels[predicted_class]

        return jsonify({'prediction': prediction_label})
    except Exception as e:
        return jsonify({'error': str(e)})

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
