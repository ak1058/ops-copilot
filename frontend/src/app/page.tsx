"use client";

import { useState, useRef, useEffect } from "react";

interface CopilotResponse {
  requestId: string;
  answer: string;
  intent: string;
  orderId?: number;
  toolsUsed: string[];
  evidence: string[];
}

interface Message {
  role: "user" | "assistant";
  content: string;
  metadata?: CopilotResponse;
  isError?: boolean;
}

export default function Home() {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: "assistant",
      content: "Hello! I am your Operations Copilot. You can ask me about order status, payment details, delivery tracking, and more."
    }
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMessage: Message = { role: "user", content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/v1/copilot/query", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: userMessage.content }),
      });

      const data = await res.json();
      
      if (!res.ok) {
        setMessages(prev => [...prev, {
          role: "assistant",
          content: data.message || "An error occurred while processing your request.",
          isError: true
        }]);
      } else {
        setMessages(prev => [...prev, {
          role: "assistant",
          content: data.answer,
          metadata: data as CopilotResponse
        }]);
      }
    } catch (error) {
      setMessages(prev => [...prev, {
        role: "assistant",
        content: "Failed to connect to the backend server. Make sure it is running on http://localhost:8080.",
        isError: true
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="flex h-screen bg-gray-900 text-gray-100 font-sans">
      <div className="flex-1 flex flex-col max-w-5xl mx-auto p-4 md:p-6 h-full">
        <header className="py-4 px-6 mb-6 bg-gray-800 rounded-xl shadow-lg border border-gray-700 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-indigo-500 bg-clip-text text-transparent">
              OpsCopilot
            </h1>
            <p className="text-sm text-gray-400">AI-Powered Operations Assistant</p>
          </div>
          <div className="flex items-center gap-2 text-sm text-gray-400">
            <span className="w-2 h-2 rounded-full bg-green-500 shadow-[0_0_8px_#22c55e]"></span>
            System Online
          </div>
        </header>

        <div className="flex-1 bg-gray-800 rounded-xl shadow-lg border border-gray-700 overflow-hidden flex flex-col">
          <div className="flex-1 overflow-y-auto p-4 space-y-6">
            {messages.map((msg, idx) => (
              <div key={idx} className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}>
                <div className={`max-w-[85%] rounded-2xl p-4 ${
                  msg.role === "user" 
                    ? "bg-blue-600 text-white rounded-br-none shadow-md" 
                    : msg.isError 
                      ? "bg-red-900/50 border border-red-800 text-red-200 rounded-bl-none"
                      : "bg-gray-700 text-gray-100 rounded-bl-none shadow-md border border-gray-600"
                }`}>
                  <div className="mb-1 text-sm opacity-70 flex items-center gap-2">
                    {msg.role === "user" ? "You" : "Copilot"}
                  </div>
                  <p className="whitespace-pre-wrap leading-relaxed">{msg.content}</p>
                  
                  {msg.metadata && msg.metadata.toolsUsed && msg.metadata.toolsUsed.length > 0 && (
                    <div className="mt-4 pt-3 border-t border-gray-600/50">
                      <p className="text-xs text-gray-400 mb-2 font-medium uppercase tracking-wider">Tools Executed:</p>
                      <div className="flex flex-wrap gap-2">
                        {msg.metadata.toolsUsed.map((tool, i) => (
                          <span key={i} className="px-2 py-1 text-xs bg-gray-900 border border-gray-600 rounded text-emerald-400 flex items-center gap-1">
                            <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path></svg>
                            {tool}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex justify-start">
                <div className="bg-gray-700 text-gray-100 rounded-2xl rounded-bl-none p-4 shadow-md border border-gray-600">
                  <div className="flex gap-1 items-center h-6">
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></span>
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></span>
                    <span className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></span>
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <div className="p-4 bg-gray-800 border-t border-gray-700">
            <form onSubmit={handleSubmit} className="flex gap-3">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask about order #1289..."
                className="flex-1 bg-gray-900 border border-gray-600 rounded-lg px-4 py-3 text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all shadow-inner placeholder-gray-500"
                disabled={isLoading}
              />
              <button 
                type="submit" 
                disabled={isLoading || !input.trim()}
                className="bg-blue-600 hover:bg-blue-500 disabled:bg-gray-600 disabled:cursor-not-allowed text-white px-6 py-3 rounded-lg font-medium transition-colors shadow-md flex items-center justify-center min-w-[100px]"
              >
                {isLoading ? "..." : "Send"}
              </button>
            </form>
            <div className="mt-3 flex gap-2 overflow-x-auto pb-1 hide-scrollbar text-sm text-gray-400">
              <span className="text-gray-500 mr-1 whitespace-nowrap py-1">Try:</span>
              <button type="button" onClick={() => setInput("What's the payment status for order #4521?")} className="whitespace-nowrap bg-gray-900 hover:bg-gray-700 px-3 py-1 rounded-full border border-gray-700 transition-colors">What's the payment status for order #4521?</button>
              <button type="button" onClick={() => setInput("Customer says they've paid for order #1289 but delivery isn't scheduled.")} className="whitespace-nowrap bg-gray-900 hover:bg-gray-700 px-3 py-1 rounded-full border border-gray-700 transition-colors">Order #1289 delivery missing</button>
              <button type="button" onClick={() => setInput("Give me a full status summary for order #2231.")} className="whitespace-nowrap bg-gray-900 hover:bg-gray-700 px-3 py-1 rounded-full border border-gray-700 transition-colors">Status of #2231</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
