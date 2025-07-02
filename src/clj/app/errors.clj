(ns app.errors
  "Helper functions for throwing consistent application exceptions.

   These functions ensure that all exceptions thrown throughout the application
   have a consistent structure and type, making them easier to handle by the
   exception middleware.")

(defn not-found!
  "Throw a not-found exception with the given message and optional data."
  ([message]
   (not-found! message {}))
  ([message data]
   (throw (ex-info message (assoc data :type :system.exception/not-found)))))

(defn business-error!
  "Throw a business logic exception with the given message and optional data."
  ([message]
   (business-error! message {}))
  ([message data]
   (throw (ex-info message (assoc data :type :system.exception/business)))))

(defn internal-error!
  "Throw an internal server exception with the given message and optional data."
  ([message]
   (internal-error! message {}))
  ([message data]
   (throw (ex-info message (assoc data :type :system.exception/internal)))))

(defn unauthorized!
  "Throw an unauthorized exception with the given message and optional data."
  ([message]
   (unauthorized! message {}))
  ([message data]
   (throw (ex-info message (assoc data :type :system.exception/unauthorized)))))

(defn forbidden!
  "Throw a forbidden exception with the given message and optional data."
  ([message]
   (forbidden! message {}))
  ([message data]
   (throw (ex-info message (assoc data :type :system.exception/forbidden)))))
