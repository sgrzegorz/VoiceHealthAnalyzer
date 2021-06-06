# skrypt minimalny do rozpoznania próbki
import tensorflow as tf


model = tf.keras.models.load_model('my_model')
recording = '/content/real_recording/healthy/recording.wav'

audio_binary = tf.io.read_file(recording)
audio, _ = tf.audio.decode_wav(audio_binary)
waveform = tf.squeeze(audio, axis=-1)
zero_padding = tf.zeros([500000] - tf.shape(waveform), dtype=tf.float32)

waveform = tf.cast(waveform, tf.float32)
equal_length = tf.concat([waveform, zero_padding], 0)
spectrogram = tf.signal.stft(equal_length, frame_length=255, frame_step=128)
spectrogram = tf.abs(spectrogram)
spectrogram = tf.expand_dims(spectrogram, -1)

prediction = model(spectrogram)
prediction =tf.nn.softmax(prediction[0])
print(prediction)
