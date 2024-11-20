import React, { useState, useEffect } from "react";
import axios from "axios"; // Import Axios
import "./App.css";

function App() {
  const [transferHistory, setTransferHistory] = useState([]);
  const [form, setForm] = useState({ account: "", amount: "" });
  const [userDetails, setUserDetails] = useState({
    name: "",
    accountNumber: "",
    balance: 0,
  });

  // Fetch user details when the component loads
  useEffect(() => {
    const fetchUserDetails = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/user/1234567890"
        );
        setUserDetails(response.data);
      } catch (error) {
        console.error("Failed to fetch user details:", error);
      }
    };

    fetchUserDetails();
  }, []);

  // Fetch transfer history
  useEffect(() => {
    const fetchTransferHistory = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/user/1234567890/transfers"
        );
        setTransferHistory(response.data);
      } catch (error) {
        console.error("Failed to fetch transfer history:", error);
      }
    };

    fetchTransferHistory();
  }, []);

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  // Handle money transfer
  const handleTransfer = async (e) => {
    e.preventDefault();
    if (form.account && form.amount) {
      try {
        const response = await axios.post("http://localhost:8080/api/transfer", {
          user: { accountNumber: userDetails.accountNumber }, // Sending user info
          recipientAccountNumber: form.account, // Sending recipient account
          amount: parseFloat(form.amount), // Sending transfer amount
        });

        // Update balance and transfer history
        setUserDetails((prev) => ({
          ...prev,
          balance: prev.balance - parseFloat(form.amount),
        }));
        setTransferHistory([response.data, ...transferHistory]);
        setForm({ account: "", amount: "" });
      } catch (error) {
        console.error("Transfer failed:", error);
        alert("Transfer failed. Please try again.");
      }
    } else {
      alert("Please fill in all fields.");
    }
  };

  return (
    <div className="bank-app">
      {/* Header */}
      <header className="bank-header">
        <img src="/logo.png" alt="Bank Logo" className="bank-logo" />
        <div className="user-info">
          <h2>{userDetails.name}</h2>
          <p>Account Number: {userDetails.accountNumber}</p>
          <p>Balance: ${userDetails.balance.toFixed(2)}</p>
        </div>
      </header>

      {/* Transfer Money Card */}
      <div className="transfer-card">
        <h3>Transfer Money</h3>
        <form onSubmit={handleTransfer}>
          <input
            type="text"
            name="account"
            placeholder="Recipient's Account Number"
            value={form.account}
            onChange={handleInputChange}
          />
          <input
            type="number"
            name="amount"
            placeholder="Amount"
            value={form.amount}
            onChange={handleInputChange}
          />
          <button type="submit">Transfer Money</button>
        </form>
      </div>

      {/* Transfer History */}
      <div className="transfer-history">
        <h3>Transfer History</h3>
        <ul>
          {transferHistory.map((transfer, index) => (
            <li key={index}>
              <p>To: {transfer.recipientAccountNumber}</p>
              <p>Amount: ${transfer.amount}</p>
              <p>Date: {new Date(transfer.date).toLocaleString()}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default App;
