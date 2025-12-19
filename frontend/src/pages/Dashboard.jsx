import React, { useState, useEffect } from 'react';
import { bookingService } from '../services/api';
import { Search, Train, Calendar, CheckCircle, XCircle, MapPin, Download, Trash2, Clock, User, ArrowRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';

import BookingModal from '../components/BookingModal';

const Dashboard = () => {
    const { user } = useAuth();
    const [stations, setStations] = useState([]);
    const [myBookings, setMyBookings] = useState([]);
    const [searchParams, setSearchParams] = useState({ sourceId: '', destinationId: '', date: '' });
    const [searchResults, setSearchResults] = useState([]);
    const [loadingBookings, setLoadingBookings] = useState(true);
    const [loadingSearch, setLoadingSearch] = useState(false);
    const [message, setMessage] = useState('');
    const [selectedSchedule, setSelectedSchedule] = useState(null); // For BookingModal

    useEffect(() => {
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        try {
            const [stationsRes, bookingsRes] = await Promise.all([
                bookingService.getAllStations(),
                bookingService.getUserBookings()
            ]);
            setStations(stationsRes.data);
            setMyBookings(bookingsRes.data);
        } catch (error) {
            console.error("Failed to load data", error);
            setMessage('Failed to load initial data. Is backend running?');
        } finally {
            setLoadingBookings(false);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        setLoadingSearch(true);
        setMessage('');
        try {
            const response = await bookingService.searchSchedules(
                searchParams.sourceId,
                searchParams.destinationId,
                searchParams.date
            );
            setSearchResults(response.data);
            if (response.data.length === 0) setMessage('No trains found for this route/date.');
        } catch (error) {
            setMessage('Search failed.');
        } finally {
            setLoadingSearch(false);
        }
    };

    const handleBookingSuccess = async () => {
        alert('Booking Successful!');
        setSearchResults([]);
        await loadInitialData(); // Reload bookings
    };

    return (
        <div className="space-y-10 pb-12">
            <header className="flex justify-between items-end">
                <div>
                    <h1 className="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">
                        Dashboard
                    </h1>
                    <p className="text-gray-500 mt-2 text-lg">Hello, <span className="font-semibold text-gray-800">{user?.firstName || user?.email?.split('@')[0]}</span>. Where to today?</p>
                </div>
                <div className="hidden md:block">
                    <div className="bg-white p-2 rounded-lg shadow-sm border border-gray-100 flex items-center text-sm text-gray-500">
                        <Clock className="w-4 h-4 mr-2 text-indigo-500" />
                        {new Date().toLocaleDateString()}
                    </div>
                </div>
            </header>

            {message && (
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="bg-yellow-50 border-l-4 border-yellow-400 p-4 text-yellow-700 rounded-r-md"
                >
                    <p className="font-medium">{message}</p>
                </motion.div>
            )}

            {/* Search Section */}
            <section className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl border border-gray-100 p-8">
                <h2 className="text-2xl font-bold mb-6 flex items-center text-gray-800">
                    <div className="p-2 bg-indigo-100 rounded-lg mr-3">
                        <Search className="text-indigo-600 w-6 h-6" />
                    </div>
                    Find Your Train
                </h2>
                <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-6 items-end">
                    <div className="relative group">
                        <label className="block text-sm font-semibold text-gray-700 mb-2">From</label>
                        <div className="relative">
                            <MapPin className="absolute left-3 top-3 text-gray-400 w-5 h-5 group-hover:text-indigo-500 transition-colors" />
                            <select
                                className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none appearance-none cursor-pointer hover:bg-white"
                                value={searchParams.sourceId}
                                onChange={e => setSearchParams({ ...searchParams, sourceId: e.target.value })}
                                required
                            >
                                <option value="">Select Source</option>
                                {stations.map(s => <option key={s.id} value={s.id}>{s.stationName}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="relative group">
                        <label className="block text-sm font-semibold text-gray-700 mb-2">To</label>
                        <div className="relative">
                            <MapPin className="absolute left-3 top-3 text-gray-400 w-5 h-5 group-hover:text-pink-500 transition-colors" />
                            <select
                                className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none appearance-none cursor-pointer hover:bg-white"
                                value={searchParams.destinationId}
                                onChange={e => setSearchParams({ ...searchParams, destinationId: e.target.value })}
                                required
                            >
                                <option value="">Select Destination</option>
                                {stations.map(s => <option key={s.id} value={s.id}>{s.stationName}</option>)}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Date</label>
                        <input
                            type="date"
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all outline-none hover:bg-white"
                            value={searchParams.date}
                            onChange={e => setSearchParams({ ...searchParams, date: e.target.value })}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={loadingSearch}
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-6 rounded-xl shadow-lg shadow-indigo-200 hover:shadow-indigo-300 transition-all transform hover:-translate-y-0.5 active:translate-y-0 flex justify-center items-center gap-2"
                    >
                        {loadingSearch ? 'Searching...' : <><Search className="w-5 h-5" /> Search Trains</>}
                    </button>
                </form>
            </section>

            {/* Search Results */}
            <AnimatePresence>
                {searchResults.length > 0 && (
                    <motion.section
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -20 }}
                        className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden"
                    >
                        <div className="p-6 bg-gray-50 border-b border-gray-100">
                            <h2 className="text-xl font-bold text-gray-800">Available Trains</h2>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-100">
                                <thead className="bg-white">
                                    <tr>
                                        <th className="px-8 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Train Details</th>
                                        <th className="px-8 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Departure</th>
                                        <th className="px-8 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Arrival</th>
                                        <th className="px-8 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Seats</th>
                                        <th className="px-8 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">Action</th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-50">
                                    {searchResults.map((schedule) => (
                                        <tr key={schedule.id} className="hover:bg-indigo-50/30 transition-colors">
                                            <td className="px-8 py-6 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    <div className="flex-shrink-0 h-10 w-10 bg-indigo-100 rounded-full flex items-center justify-center text-indigo-600">
                                                        <Train className="w-5 h-5" />
                                                    </div>
                                                    <div className="ml-4">
                                                        <div className="text-sm font-bold text-gray-900">{schedule.train.trainName}</div>
                                                        <div className="text-xs text-gray-500">{schedule.train.trainNumber}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-8 py-6 whitespace-nowrap text-sm text-gray-700 font-medium">{schedule.departureTime}</td>
                                            <td className="px-8 py-6 whitespace-nowrap text-sm text-gray-700 font-medium">{schedule.arrivalTime}</td>
                                            <td className="px-8 py-6 whitespace-nowrap">
                                                <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${schedule.availableSeats > 10 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                                                    {schedule.availableSeats} Seats
                                                </span>
                                            </td>
                                            <td className="px-8 py-6 whitespace-nowrap text-right text-sm font-medium">
                                                <button
                                                    onClick={() => setSelectedSchedule(schedule)}
                                                    className="text-indigo-600 hover:text-indigo-900 font-bold hover:bg-indigo-50 px-4 py-2 rounded-lg transition-colors border border-transparent hover:border-indigo-100"
                                                >
                                                    Book Ticket
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </motion.section>
                )}
            </AnimatePresence>

            {/* My Bookings */}
            <section>
                <h2 className="text-2xl font-bold mb-6 flex items-center text-gray-800">
                    <div className="p-2 bg-purple-100 rounded-lg mr-3">
                        <Calendar className="text-purple-600 w-6 h-6" />
                    </div>
                    Your Journey History
                </h2>
                {loadingBookings ? (
                    <div className="flex justify-center p-12">
                        <div className="w-8 h-8 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
                    </div>
                ) : myBookings.length === 0 ? (
                    <div className="text-center py-12 bg-gray-50 rounded-2xl border-2 border-dashed border-gray-200">
                        <Train className="mx-auto h-12 w-12 text-gray-300" />
                        <h3 className="mt-2 text-sm font-medium text-gray-900">No bookings yet</h3>
                        <p className="mt-1 text-sm text-gray-500">Get started by searching for a train above.</p>
                    </div>
                ) : (
                    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                        {myBookings.map((booking) => (
                            <motion.div
                                whileHover={{ y: -5 }}
                                key={booking.id}
                                className="bg-white rounded-2xl shadow-md hover:shadow-xl border border-gray-100 transition-all overflow-hidden flex flex-col"
                            >
                                <div className="p-6 flex-1">
                                    <div className="flex justify-between items-start mb-4">
                                        <div>
                                            <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">PNR Number</span>
                                            <p className="text-xl font-mono font-bold text-gray-800">{booking.pnrNumber}</p>
                                        </div>
                                        <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-700' :
                                            booking.status === 'CANCELLED' ? 'bg-red-100 text-red-700' : 'bg-gray-100 text-gray-700'
                                            }`}>
                                            {booking.status}
                                        </span>
                                    </div>

                                    <div className="space-y-3">
                                        <div className="flex items-center text-gray-600">
                                            <Calendar className="w-4 h-4 mr-2" />
                                            <span className="text-sm">{booking.journeyDate}</span>
                                        </div>
                                        <div className="flex items-center justify-between text-sm">
                                            <div className="flex flex-col">
                                                <span className="text-xs text-gray-400">From</span>
                                                <span className="font-semibold text-gray-800">{booking.sourceStation}</span>
                                            </div>
                                            <ArrowRight className="text-gray-300 w-4 h-4" />
                                            <div className="flex flex-col text-right">
                                                <span className="text-xs text-gray-400">To</span>
                                                <span className="font-semibold text-gray-800">{booking.destinationStation}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="bg-gray-50 px-6 py-4 border-t border-gray-100 flex justify-between items-center">
                                    <span className="font-bold text-lg text-gray-900">${booking.totalFare}</span>
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => window.open(`/api/bookings/${booking.id}/ticket`, '_blank')}
                                            className="p-2 text-indigo-600 hover:bg-indigo-100 rounded-lg transition-colors"
                                            title="Download Ticket"
                                        >
                                            <Download className="w-5 h-5" />
                                        </button>
                                        {booking.status !== 'CANCELLED' && (
                                            <button
                                                onClick={async () => {
                                                    if (confirm('Cancel this booking?')) {
                                                        await bookingService.cancelBooking(booking.id);
                                                        loadInitialData();
                                                    }
                                                }}
                                                className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                                title="Cancel Booking"
                                            >
                                                <Trash2 className="w-5 h-5" />
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                )}
            </section>

            {/* Booking Modal */}
            <AnimatePresence>
                {selectedSchedule && (
                    <BookingModal
                        schedule={selectedSchedule}
                        onClose={() => setSelectedSchedule(null)}
                        onSuccess={handleBookingSuccess}
                    />
                )}
            </AnimatePresence>
        </div >
    );
};

export default Dashboard;
