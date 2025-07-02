(ns app.middlewares.response
  "Middleware for standardizing API response format")

(defn wrap-response-envelope
  "Wraps successful responses in a standard envelope format.
   Transforms: {:status 200 :body {...}}
   Into: {:status 200 :body {:status \"success\" :data {...}}}"
  [handler]
  (fn [request]
    (let [response (handler request)
          {:keys [status body]} response]
      (if (and (>= status 200) (< status 300))
        ;; Success response - wrap in envelope
        (assoc response :body {:status "success" :data body})
        ;; Error response - let exception middleware handle it
        response))))
