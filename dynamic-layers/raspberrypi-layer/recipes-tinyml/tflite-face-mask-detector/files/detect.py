import sys
import os
import argparse
import time

import cv2
import numpy as np
from tflite_runtime.interpreter import Interpreter

type_list = ['got mask', 'no mask', 'misplaced mask']
window_width = 640
window_height = 480

def model_init(model_path):
	interpreter = Interpreter(model_path=model_path)
	interpreter.allocate_tensors()
	input_details = interpreter.get_input_details()
	output_details = interpreter.get_output_details()
	return interpreter, input_details, output_details

def imread(img, shape):
	if img is not None:
		img_ = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
		img_ = cv2.resize((img_*2/255)-1, (shape,shape))
		img_ = img_[np.newaxis,:,:,:].astype('float32')
		return img_

def get_output(interpreter, output_details, input_details, image, shape):
	output_frame = imread(image, shape)
	interpreter.set_tensor(input_details[0]['index'], output_frame)
	interpreter.invoke()
	boxes = interpreter.get_tensor(output_details[0]['index'])
	classes = interpreter.get_tensor(output_details[1]['index'])
	scores = interpreter.get_tensor(output_details[2]['index'])
	num = interpreter.get_tensor(output_details[3]['index'])
	return boxes, classes, scores, num

def draw_and_show(boxes, classes, scores, num, frame):
	for i in range(int(num[0])):
		# print(scores[0][int(i)-1])
		if scores[0][i] > 0.8:
			y, x, bottom, right = boxes[0][i]
			x, right = int(x * window_width), int(right * window_width)
			y, bottom = int(y * window_height), int(bottom * window_height)
			class_type = type_list[int(classes[0][i])]
			label_size = cv2.getTextSize(class_type, cv2.FONT_HERSHEY_DUPLEX, 0.5, 1)
			cv2.rectangle(frame, (x, y), (right, bottom), (0, 255, 0), thickness=2)
			cv2.rectangle(frame,(x, y - 18), (x + label_size[0][0], y), (0, 255, 0),thickness=-1)
			cv2.putText(frame, class_type, (x,y-5), cv2.FONT_HERSHEY_DUPLEX, 0.5, (0, 0, 0), 1, cv2.LINE_AA)
	return frame

def run(model_path, camera_id) -> None:
	# Start capturing video input from the camera
	cap = cv2.VideoCapture(camera_id)
	cap.set(cv2.CAP_PROP_FRAME_WIDTH, window_width)
	cap.set(cv2.CAP_PROP_FRAME_HEIGHT, window_height)

	# Initialize the face mask detection model
	interpreter, input_details, output_details = model_init(model_path)

	# Continuously capture images from the camera and run inference
	while cap.isOpened():
		# Capture image from camera
		success, image = cap.read()
		if not success:
			sys.exit('ERROR: Unable to read from webcam. Please verify your webcam settings.')

		# Flip capured image horizontally
		image = cv2.flip(image, 1)

		# Run face mask detection using the model
		boxes, classes, scores, num = get_output(interpreter, output_details, input_details, image, input_details[0]['shape'][1])

		# Draw keypoints and edges on input image
		frame = draw_and_show(boxes, classes, scores, num, image)

		# Display image in a window
		cv2.imshow('face_mask_detector', frame)

		# Stop the program if the ESC key is pressed
		if cv2.waitKey(1) == 27:
			break

	cap.release()
	cv2.destroyAllWindows()

def main():
	parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
	parser.add_argument(
		'--model',
		help='Path of the object detection model.',
		required=False,
		default=os.path.join(os.getcwd(), 'ssd_mobilenet_v2_fpnlite.tflite'))
	parser.add_argument(
		'--cameraId', 
		help='Id of camera.', 
		required=False, 
		type=int, 
		default=0)
	args = parser.parse_args()

	run(args.model, args.cameraId)

if __name__ == '__main__':
	main()
