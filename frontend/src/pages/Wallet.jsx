import React, { useState, useEffect } from 'react';
import { walletService } from '../services/api';
import { CreditCard, Plus, ArrowUpRight, ArrowDownLeft, Clock, Wallet as WalletIcon } from 'lucide-react';
import { motion } from 'framer-motion';

const Wallet = () => {
    const [balance, setBalance] = useState(0);
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [amountToAdd, setAmountToAdd] = useState('');
    const [addingFunds, setAddingFunds] = useState(false);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchWalletData();
    }, []);

    const fetchWalletData = async () => {
        try {
            const [walletRes, txnRes] = await Promise.all([
                walletService.getWallet(),
                walletService.getTransactions()
            ]);
            setBalance(walletRes.data.balance);
            setTransactions(txnRes.data);
        } catch (error) {
            console.error("Failed to fetch wallet data", error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddFunds = async (e) => {
        e.preventDefault();
        if (!amountToAdd || isNaN(amountToAdd) || amountToAdd <= 0) return;

        setAddingFunds(true);
        setMessage('');
        try {
            await walletService.addFunds(parseFloat(amountToAdd), 'CREDIT_CARD');
            setMessage('Funds added successfully!');
            setAmountToAdd('');
            fetchWalletData();
        } catch (error) {
            setMessage('Failed to add funds.');
        } finally {
            setAddingFunds(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto space-y-8">
            <header>
                <h1 className="text-3xl font-bold text-gray-900">My Wallet</h1>
                <p className="text-gray-600">Manage your funds and transactions</p>
            </header>

            <div className="grid gap-8 md:grid-cols-2">
                {/* Balance Card */}
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="bg-gradient-to-br from-indigo-600 to-purple-700 rounded-2xl p-8 text-white shadow-xl relative overflow-hidden"
                >
                    <div className="absolute top-0 right-0 p-4 opacity-10">
                        <WalletIcon className="w-32 h-32" />
                    </div>
                    <div className="relative z-10">
                        <p className="text-indigo-100 font-medium mb-1">Total Balance</p>
                        <h2 className="text-5xl font-bold mb-6">${balance.toFixed(2)}</h2>

                        <form onSubmit={handleAddFunds} className="flex gap-2">
                            <input
                                type="number"
                                min="1"
                                placeholder="Amount"
                                className="w-32 px-4 py-2 rounded-lg text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-300"
                                value={amountToAdd}
                                onChange={(e) => setAmountToAdd(e.target.value)}
                            />
                            <button
                                type="submit"
                                disabled={addingFunds}
                                className="bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2"
                            >
                                {addingFunds ? <div className="w-4 h-4 border-2 border-white/50 border-t-white rounded-full animate-spin" /> : <Plus className="w-4 h-4" />}
                                Add Funds
                            </button>
                        </form>
                        {message && <p className="mt-2 text-sm text-green-200">{message}</p>}
                    </div>
                </motion.div>

                {/* Quick Info / Stats (Optional) */}
                <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100 flex flex-col justify-center">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">Payment Methods</h3>
                    <div className="flex items-center gap-4 p-4 bg-gray-50 rounded-xl border border-gray-200">
                        <div className="bg-indigo-100 p-2 rounded-full">
                            <CreditCard className="w-6 h-6 text-indigo-600" />
                        </div>
                        <div>
                            <p className="font-bold text-gray-800">MasterCard ending in 4242</p>
                            <p className="text-sm text-gray-500">Expires 12/29</p>
                        </div>
                    </div>
                    <p className="mt-4 text-sm text-gray-500">
                        This is a mock application. No real money is deducted.
                    </p>
                </div>
            </div>

            {/* Transactions */}
            <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="p-6 border-b border-gray-100">
                    <h3 className="text-lg font-bold text-gray-900">Transaction History</h3>
                </div>

                {loading ? (
                    <div className="p-8 text-center text-gray-500">Loading transactions...</div>
                ) : transactions.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">No transactions yet.</div>
                ) : (
                    <div className="divide-y divide-gray-100">
                        {transactions.map((txn) => (
                            <div key={txn.transactionId} className="p-4 hover:bg-gray-50 transition-colors flex items-center justify-between">
                                <div className="flex items-center gap-4">
                                    <div className={`p-2 rounded-full ${txn.type === 'CREDIT' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'}`}>
                                        {txn.type === 'CREDIT' ? <ArrowDownLeft className="w-5 h-5" /> : <ArrowUpRight className="w-5 h-5" />}
                                    </div>
                                    <div>
                                        <p className="font-medium text-gray-900">{txn.description}</p>
                                        <div className="flex items-center text-xs text-gray-500 gap-2">
                                            <span>{txn.transactionId}</span>
                                            <span>•</span>
                                            <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> {new Date(txn.createdAt).toLocaleDateString()}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className={`font-bold ${txn.type === 'CREDIT' ? 'text-green-600' : 'text-gray-900'}`}>
                                    {txn.type === 'CREDIT' ? '+' : '-'}${txn.amount.toFixed(2)}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
};

export default Wallet;
