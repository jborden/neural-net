(ns neural-net.keyboard
  (:import java.awt.Robot
           java.awt.event.KeyEvent))

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

(defn keytype
  "Given a string consisting only of a-z,A-Z,0-9, type them to the keyboard"
  [str]
  (let [robot (new Robot)]
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
            (.keyRelease (int upCh))))))))

(defn hit-esc
  "Hit the escape button"
  []
  (let [robot (new Robot)]
    (doto robot
      (.keyPress (. KeyEvent VK_ESCAPE))
      (.delay 50)
      (.keyRelease (. KeyEvent VK_ESCAPE)))))

;; you can delay executio of the thread and type in another window:
;; (do (Thread/sleep 3000) (keytype "1234567890"))
