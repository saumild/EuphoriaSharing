from keras.models import Sequential
#from keras.layers.convolutional import Conv2D
#from keras.layers import MaxPolling2D  
from keras.layers import Conv2D, MaxPooling2D
from keras.layers import Flatten,Dropout
from keras.layers import Dense
from keras.preprocessing.image import ImageDataGenerator

#Data shuffled and split between train and test
#(x_train, y_train), (x_test, y_test) = cifar10.load_data()
#print('x_train shape:', x_train.shape)
#print(x_train.shape[0], 'train samples')
#print(x_test.shape[0], 'test samples')

classifier = Sequential()

classifier.add(Conv2D(64,(3,3),input_shape = (128,128,3), activation = 'relu'))
classifier.add(MaxPooling2D(pool_size = (2,2)))


classifier.add(Conv2D(64,(3,3), activation = 'relu'))
classifier.add(MaxPooling2D(pool_size = (2,2)))

classifier.add(Conv2D(32,(3,3), activation = 'relu'))
classifier.add(MaxPooling2D(pool_size = (2,2)))

classifier.add(Conv2D(32,(3,3), activation = 'relu'))
classifier.add(MaxPooling2D(pool_size = (2,2)))


classifier.add(Flatten())

classifier.add(Dense(32 , activation='relu'))
classifier.add(Dropout(0.5))
classifier.add(Dense(1, activation='sigmoid'))

classifier.compile(optimizer='adam',loss = 'binary_crossentropy', metrics = ['accuracy'])

train_datagen = ImageDataGenerator(
       rotation_range=40,
        width_shift_range=0.2,
        height_shift_range=0.2,
        rescale=1./255,
        shear_range=0.2,
        zoom_range=0.2,
        horizontal_flip=True,
        fill_mode='nearest')

test_datagen = ImageDataGenerator(rescale=1./255)

training_set = train_datagen.flow_from_directory(
        'Dataset/training-set',
        target_size=(128, 128),
        batch_size=32,
        class_mode='binary')

testing_set = test_datagen.flow_from_directory(
        'Dataset/testing-set',
        target_size=(128, 128),
        batch_size=32,
        class_mode='binary')

classifier.fit_generator(
        training_set,
        steps_per_epoch=205,
        epochs=10,
        validation_data=testing_set,
         validation_steps=68)
