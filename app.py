from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/generate', methods=['POST'])
def generate():
    data = request.json
    prompt = data.get('prompt', '')
    answer = f"Odpověď na '{prompt}' od simulovaného LLM."
    return jsonify({"response": answer})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)

