import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Menu, X, Train, LogOut, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';

const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isActive = (path) => location.pathname === path;

    return (
        <nav className="bg-white/80 backdrop-blur-md sticky top-0 z-50 border-b border-gray-100">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex items-center">
                        <Link to="/" className="flex-shrink-0 flex items-center gap-2 group">
                            <div className="bg-indigo-600 p-1.5 rounded-lg group-hover:bg-indigo-700 transition-colors">
                                <Train className="h-6 w-6 text-white" />
                            </div>
                            <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-700 to-purple-700">
                                TrainTicketing
                            </span>
                        </Link>
                    </div>

                    <div className="hidden md:flex items-center space-x-6">
                        {user ? (
                            <>
                                <Link
                                    to="/dashboard"
                                    className={`px-3 py-2 rounded-md text-sm font-medium transition-all ${isActive('/dashboard')
                                        ? 'text-indigo-600 bg-indigo-50'
                                        : 'text-gray-600 hover:text-indigo-600 hover:bg-gray-50'
                                        }`}
                                >
                                    Dashboard
                                </Link>
                                <Link
                                    to="/wallet"
                                    className={`px-3 py-2 rounded-md text-sm font-medium transition-all ${isActive('/wallet')
                                        ? 'text-indigo-600 bg-indigo-50'
                                        : 'text-gray-600 hover:text-indigo-600 hover:bg-gray-50'
                                        }`}
                                >
                                    Wallet
                                </Link>
                                {user?.role === 'ADMIN' && (
                                    <Link
                                        to="/admin"
                                        className={`px-3 py-2 rounded-md text-sm font-medium transition-all ${isActive('/admin')
                                            ? 'text-indigo-600 bg-indigo-50'
                                            : 'text-gray-600 hover:text-indigo-600 hover:bg-gray-50'
                                            }`}
                                    >
                                        Admin
                                    </Link>
                                )}
                                <div className="flex items-center space-x-4 pl-4 border-l border-gray-200">
                                    <div className="flex items-center space-x-2">
                                        <div className="h-8 w-8 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-white text-xs font-bold shadow-sm">
                                            {user.email?.charAt(0).toUpperCase()}
                                        </div>
                                        <span className="text-sm font-medium text-gray-700">{user.firstName || user.email?.split('@')[0]}</span>
                                    </div>
                                    <button
                                        onClick={handleLogout}
                                        className="p-2 rounded-full hover:bg-red-50 text-gray-400 hover:text-red-500 transition-all"
                                        title="Logout"
                                    >
                                        <LogOut className="h-5 w-5" />
                                    </button>
                                </div>
                            </>
                        ) : (
                            <div className="flex items-center space-x-4">
                                <Link to="/login" className="text-gray-600 hover:text-indigo-600 font-medium transition-colors">Login</Link>
                                <Link
                                    to="/register"
                                    className="bg-indigo-600 text-white hover:bg-indigo-700 px-5 py-2.5 rounded-full text-sm font-bold shadow-md hover:shadow-lg transition-all transform hover:-translate-y-0.5"
                                >
                                    Get Started
                                </Link>
                            </div>
                        )}
                    </div>

                    <div className="flex items-center md:hidden">
                        <button
                            onClick={() => setIsOpen(!isOpen)}
                            className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none"
                        >
                            <span className="sr-only">Open main menu</span>
                            {isOpen ? <X className="block h-6 w-6" /> : <Menu className="block h-6 w-6" />}
                        </button>
                    </div>
                </div>
            </div>

            {/* Mobile menu */}
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: 'auto', opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        className="md:hidden overflow-hidden bg-white border-t border-gray-100"
                    >
                        <div className="px-4 pt-2 pb-3 space-y-1">
                            {user ? (
                                <>
                                    <Link to="/dashboard" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-indigo-600 hover:bg-gray-50">Dashboard</Link>
                                    <button onClick={handleLogout} className="w-full text-left block px-3 py-2 rounded-md text-base font-medium text-red-600 hover:bg-red-50">Logout</button>
                                </>
                            ) : (
                                <>
                                    <Link to="/login" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50">Login</Link>
                                    <Link to="/register" className="block px-3 py-2 rounded-md text-base font-medium text-indigo-600 hover:bg-indigo-50">Register</Link>
                                </>
                            )}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </nav>
    );
};

export default Navbar;
