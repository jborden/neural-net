(ns neural-net.keyboard
  (:import java.awt.Robot
           java.awt.event.KeyEvent))

;; It would be better if we had ONE robot object and designed an interface to it
;; then all keys would be done through the robot obj instead of creating new
;; ones for EACH key event

;; perhaps something along the lines the clj-webdriver selenium wrapper?
(def robot (new Robot))

;; from http://rosettacode.org/wiki/Simulate_input/Keyboard#Clojure
;; just a start, only does a-z,A-Z,0-9 does NOT handle special chars like ()
;; perhaps a delay would also help the output be more natural
;; see: http://stackoverflow.com/questions/25909039/java-awt-robot-inside-games
;; package MyProject;

;; import java.awt.AWTException;
;; import java.awt.Robot;
;; import java.awt.event.KeyEvent;
;;
;; public class KeyStroke {
;;     private static Robot robot;
;;
;;     public static void main(String[] args) throws AWTException {
;;         robot = new Robot();
;;         robot.delay(3000);
;;         keystroke(KeyEvent.VK_Q);
;;         keystroke(KeyEvent.VK_W);
;;         keystroke(KeyEvent.VK_E);
;;         keystroke(KeyEvent.VK_R);
;;         keystroke(KeyEvent.VK_T);
;;         keystroke(KeyEvent.VK_Y);
;;     }
;;
;;     private static void keystroke(int key) {
;;         robot.keyPress(key);
;;         robot.delay(100); // hold for a tenth of a second, adjustable
;;         robot.keyRelease(key);
;;     }
;; }
;; JavaFX desktop version might be good
;;
;; there is also these libraries:
;; https://github.com/semperos/robot-remote-server-clj
;; https://github.com/calebmpeterson/awtbot
;;

;; It would be better if we had ONE robot object and designed an interface to it
;; then all keys would be done through the robot obj instead of creating new
;; ones for EACH key event

(defn keytype!
  "Given a string consisting only of a-z,A-Z,0-9, type them to the keyboard"
  [str]
  (doseq [ch str]
    (if (Character/isUpperCase ch)
      (doto robot
        (.keyPress (. KeyEvent VK_SHIFT))
        (.keyPress (int ch))
        (.delay 50)
        (.keyRelease (int ch))
        (.keyRelease (. KeyEvent VK_SHIFT)))
      (let [upCh (Character/toUpperCase ch)]
        (doto robot
          (.keyPress (int upCh))
          (.delay 50)
          (.keyRelease (int upCh)))))))

;; see https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
;; Some key codes:
;; VK_UP, VK_DOWN, VK_LEFT, VK_RIGHT, VK_CONTROL, VK_ESCAPE, VK_ALT,
;; VK_ENTER
(def key->key-event
  {:up    (. KeyEvent VK_UP)
   :down  (. KeyEvent VK_DOWN)
   :left  (. KeyEvent VK_LEFT)
   :right (. KeyEvent VK_RIGHT)
   :ctrl  (. KeyEvent VK_CONTROL)
   :alt   (. KeyEvent VK_ALT)
   :esc   (. KeyEvent VK_ESCAPE)})

(defn hit-key!
  "Given a key-code represented in key->key-event, hit it. This means
  to quickly press and release a key"
  [key-code]
  (let [delay 50]
    (doto robot
      (.keyPress (key-code key->key-event))
      (.delay delay)
      (.keyRelease (key-code key->key-event)))))

(defn press-key!
  "Given a key-code reprensented in key->key-event, press it"
  [key-code]
  (doto robot
    (.keyPress (key-code key->key-event))))

(defn release-key!
  [key-code]
  (doto robot
    (.keyRelease (key-code key->key-event))))

;; you can delay execution of the thread and type in another window:
;; (do (Thread/sleep 3000) (keytype "1234567890"))
