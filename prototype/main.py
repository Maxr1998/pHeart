import cv2
import numpy as np
from matplotlib import pyplot as plt

# Face Video
video = cv2.VideoCapture(
    r"C:\Users\Tim\Downloads\DigitalHealth\PXL_20230327_132737028.mp4")

# video = cv2.VideoCapture(r"C:\Users\Tim\Downloads\DigitalHealth\PXL_20230329_125404515.mp4")
# video = cv2.VideoCapture(0)

PYR_LEVEL = 3
FPS = video.get(cv2.CAP_PROP_FPS)
print("FPS:", FPS)
BUFFER_SECS = 10
BUFFER_FRAMES = int(FPS * BUFFER_SECS)

buffer = np.zeros((BUFFER_FRAMES,))

FFT_BUFFER_SIZE = 1024

MIN_FREQ = 64
MAX_FREQ = 120

fft_buffer_freqs = np.fft.fftfreq(FFT_BUFFER_SIZE, 1 / FPS)
fft_mask = (fft_buffer_freqs >= MIN_FREQ / 60) & (fft_buffer_freqs <= MAX_FREQ / 60)

print(fft_buffer_freqs)
print(fft_mask)
# exit(1)
output = cv2.VideoWriter("test.mp4", fps=60,
                         fourcc=cv2.VideoWriter_fourcc(*"mp4v"),
                         frameSize=(1080, 1920))

roi = None

processed_frames = 0



while True:
    did_read, frame = video.read()

    if not did_read:
        break

    if roi is None:
        roi = cv2.selectROI("Select Forehead", frame)
        cv2.destroyWindow("Select Forehead")

    output_frame = frame.copy()
    cv2.rectangle(output_frame, (roi[0], roi[1]), (roi[0] + roi[2], roi[1] + roi[3]), (0, 0, 255), 3)
    cv2.imshow("Webcam", output_frame)
    cv2.waitKey(1)

    processed_frames += 1

    # print("Buffer populated:", processed_frames >= BUFFER_FRAMES)

    cropped_frame = frame[roi[1]:roi[1] + roi[3], roi[0]:roi[0] + roi[2]]

    yuv = cv2.cvtColor(cropped_frame, cv2.COLOR_BGR2YUV)
    y, u, v = cv2.split(yuv)

    # b, img_g, r = cv2.split(frame)


    # g = img_g[roi[1]:roi[1] + roi[3], roi[0]:roi[0] + roi[2]]
    # g = img_g

    # g = img_g[435:435 + 208, 330:330 + 480:]

    mean_g = np.mean(y)
    # TARGET_BPM = 90
    # mean_g = np.sin(processed_frames / FPS * 2 * np.pi * (TARGET_BPM/60))

    # og_g = g

    buffer = np.hstack((buffer[1:], mean_g))

    if processed_frames % BUFFER_FRAMES != 0:
        continue

    fft = np.real(np.fft.fft(buffer, axis=0, n=FFT_BUFFER_SIZE))
    fft[~fft_mask] = 0

    booster_function = np.linspace(1.0, 1.5, fft.shape[0])

    boosted_fft = fft * booster_function

    plt.xlim(40, 180)
    plt.plot(fft_buffer_freqs * 60, np.abs(boosted_fft))
    plt.show()

    print(hash(buffer.tostring()))
    print(fft_buffer_freqs[np.abs(fft).argmax(axis=0)] * 60, "bpm")

    # overlay = np.real(np.fft.ifft(fft, axis=0))[-1]  # Current image
    #
    # for i in range(PYR_LEVEL):
    #     overlay = cv2.pyrUp(overlay)
    #
    # new_g = og_g * overlay
    #
    # img_g[435:435 + 208, 330:330 + 480:] = new_g
    #
    # new_img = cv2.merge([b, img_g, r])
    #
    # window = cv2.imshow("image", new_img)
    # key = cv2.waitKey(1)
    # if key == ord('q'):
    #     break

    # output.write(new_img)

output.release()
cv2.destroyAllWindows()
